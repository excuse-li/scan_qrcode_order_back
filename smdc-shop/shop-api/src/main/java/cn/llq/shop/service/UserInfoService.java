package cn.llq.shop.service;

import cn.llq.shop.dao.MerchantMapper;
import cn.llq.shop.dao.RoleMapper;
import cn.llq.shop.dao.UserMapper;
import cn.llq.shop.dao.UserRoleMapper;
import cn.llq.shop.model.bo.UserInfoLoginBo;
import cn.llq.shop.model.pojo.MerchantPo;
import cn.llq.shop.model.pojo.Role;
import cn.llq.shop.model.pojo.UserInfoPo;
import cn.llq.shop.model.pojo.UserRole;
import cn.llq.utils.JWTUtil;
import cn.llq.utils.MD5Util;
import cn.llq.utils.RedisUtil;
import cn.llq.utils.response.ResultCode;
import cn.llq.utils.response.ResultVO;
import cn.llq.utils.response.exception.APIException;
import cn.llq.utils.status.UserStatus;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoService {


    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserMapper userMapper;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    MerchantMapper merchantMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    RoleMapper roleMapper;

    public Object login(UserInfoLoginBo userInfoLoginBo){



        UserInfoPo userInfoPo = new UserInfoPo();
        if (userInfoLoginBo.getUserName()==null){
            throw new APIException(400,"用户名格式不正确");
        }

        if (userInfoLoginBo.getPassword()==null){
            throw new APIException(400,"密码格式不正确");
        }
        userInfoPo.setUserName(userInfoLoginBo.getUserName());
        userInfoPo.setPassword(MD5Util.stringToMD5(userInfoLoginBo.getPassword()));
        List<UserInfoPo> select = userMapper.select(userInfoPo);



        if (select.size()>0){
            if (select.get(0).getStatus()!=1){
                throw new APIException(400,"用户已停用胡已删除！");
            }
            UserInfoPo userInfoPo1 = select.get(0);
            String jwt = JWTUtil.generateToken(userInfoPo1.getId() + "", "smdc", null);
            userInfoPo1.setPassword(null);
            HashMap<String, Object> map = new HashMap<>();
            map.put("type",userInfoPo.getType());
            map.put("info",userInfoPo1);
            map.put("authorization",jwt);
            redisUtil.set(jwt,map);


            return ResultVO.success(map);
        }
        throw new APIException(400,"用户名或密码不正确");
    }

    @GlobalTransactional
    public void addUser(UserInfoPo userInfoPo,String token){
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(token);
        UserInfoPo info =(UserInfoPo) map.get("info");
        userInfoPo.setStatus(UserStatus.START);
        try{
            userInfoPo.setPassword(MD5Util.stringToMD5(userInfoPo.getPassword()));
            userInfoPo.setMerchantId(info.getMerchantId());
            userMapper.insert(userInfoPo);
        }catch (Exception e){
            throw new APIException(500,"用户已存在");
        }
    }

    @GlobalTransactional
    public void deleteUserById(Long id){
        UserInfoPo userInfoPo = userMapper.selectByPrimaryKey(id);
        userInfoPo.setUserName(userInfoPo.getUserName()+"_ALREADY_DELETE");
        userInfoPo.setStatus(3);
        userMapper.updateByPrimaryKeySelective(userInfoPo);
    }

    @GlobalTransactional
    public void updateUser(UserInfoPo userInfoPo){
        if (userInfoPo.getPassword()!=null&&!"".equals(userInfoPo.getPassword())){
            userInfoPo.setPassword(MD5Util.stringToMD5(userInfoPo.getPassword()));
        }else {
            userInfoPo.setPassword(null);
        }
        if (userInfoPo.getUserName()!=null&&userInfoPo.getUserName().endsWith("_ALREADY_DELETE")){
            throw new APIException(400,"用户名格式不正确");
        }
        if (new Integer(3).equals(userInfoPo.getStatus())){
            userInfoPo.setUserName(userInfoPo.getUserName()+"_ALREADY_DELETE");
        }
        userInfoPo.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(userInfoPo);
    }

    public void loginOut(String token){
        redisUtil.del(token);
    }


    public Object listUser(Long storeId, String userName, Integer page, Integer size,String token) {
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(token);

        UserInfoPo info =(UserInfoPo) map.get("info");
        PageHelper.startPage(page,size);
        Example example = new Example(UserInfoPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andLike("userName","%"+userName+"%");
        criteria.andNotLike("userName","%\\_ALREADY\\_DELETE");
        criteria.andNotEqualTo("status", UserStatus.DELETE);
        if (!Long.valueOf(0L).equals(storeId)){
            criteria.andEqualTo("storeId",storeId);
        }
        criteria.andEqualTo("merchantId",info.getMerchantId());
        List<UserInfoPo> userInfoPos = userMapper.selectByExample(example);
        userInfoPos.forEach(userInfoPo -> {
            userInfoPo.setPassword(null);
        });
        return new PageInfo<>(userInfoPos);
    }

    public Object getUserById(Long id){
        UserInfoPo userInfoPo = userMapper.selectByPrimaryKey(id);
        userInfoPo.setPassword(null);
        return userInfoPo;
    }

    public Object checkAuth(String auth, String authorization) {
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(authorization);
        if (map==null){
            throw new APIException(501,"登录已失效");
        }

        if("s".equals(map.get("type"))){
            return true;
        }

        UserInfoPo info =(UserInfoPo) map.get("info");

        MerchantPo merchantPo = merchantMapper.selectByPrimaryKey(info.getMerchantId());

        if (merchantPo==null){
            throw new APIException(403,"商户不存在");
        }

        if (merchantPo.getLimitTime().getTime()<new Date().getTime()){
            throw new APIException(403,"已超过使用期限，请联系运营人员续费。");
        }
        Long aLong = userMapper.checkAuth(auth,info.getId());

        if (aLong!=null){
            return true;
        }
        return false;
    }

    public Object getUserInfo(String token) {
        return redisUtil.get(token);
    }

    @GlobalTransactional
    public Object setRole(UserRole userRole) {

        UserRole userRole1 = new UserRole();
        userRole1.setUserId(userRole.getUserId());
        userRoleMapper.delete(userRole1);
        userRoleMapper.insert(userRole);
        return null;
    }

    public Object getRoleList(){
        return roleMapper.selectAll();
    }

    public Object getUserRole(Long id) {
        UserRole userRole = new UserRole();
        userRole.setUserId(id);
        return userRoleMapper.selectOne(userRole);
    }
}
