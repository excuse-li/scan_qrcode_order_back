package cn.llq.shop.service;

import cn.llq.shop.dao.MerchantMapper;
import cn.llq.shop.dao.UserMapper;
import cn.llq.shop.model.pojo.MerchantPo;
import cn.llq.shop.model.pojo.UserInfoPo;
import cn.llq.utils.MD5Util;
import cn.llq.utils.response.exception.APIException;
import cn.llq.utils.status.MerchantStatus;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Service
public class MerchantService {
    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    UserMapper userMapper;

    @GlobalTransactional
    public Object addMerchant(MerchantPo merchantPo){
        merchantMapper.insertUseGeneratedKeys(merchantPo);
        UserInfoPo userInfoPo = new UserInfoPo();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        try {
            String s = PinyinHelper.toHanYuPinyinString(merchantPo.getConpanyName(), format, "", true);
            int i = 1;
            BigInteger bigInteger = new BigInteger(i+"");
            DecimalFormat decimalFormat = new DecimalFormat("000000");
            String format1 = decimalFormat.format(bigInteger);
            userInfoPo.setUserName(s+format1);
            List<UserInfoPo> select = userMapper.select(userInfoPo);
            while (select!=null&&select.size()!=0){
               i++;
                bigInteger = new BigInteger(i+"");
                format1 = decimalFormat.format(bigInteger);
                userInfoPo.setUserName(s+format1);
            }
            userInfoPo.setMerchantId(merchantPo.getId());
            userInfoPo.setJoinTime(new Date());
            userInfoPo.setPassword(MD5Util.stringToMD5("123456"));
            userInfoPo.setStoreId(0L);
            userInfoPo.setType("m");
            userInfoPo.setUpdateTime(new Date());
            userInfoPo.setSex("男");
            userMapper.insert(userInfoPo);
            return "创建成功，管理员账号为：”"+userInfoPo.getUserName()+"“,密码为：”123456“";
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
            throw new APIException(400,"参数校验失败");
        }
    }

    @GlobalTransactional
    public void updateMerchant(MerchantPo merchantPo){
        merchantMapper.updateByPrimaryKey(merchantPo);
    }

    @GlobalTransactional
    public void stopMerchant(Long id){
        MerchantPo merchant = new MerchantPo();
        merchant.setUseStatus(MerchantStatus.STOP);
        merchant.setId(id);
        merchantMapper.updateByPrimaryKey(merchant);
    }

    @GlobalTransactional
    public void startMerchant(Long id){
        MerchantPo merchant = new MerchantPo();
        merchant.setUseStatus(MerchantStatus.RUN);
        merchant.setId(id);
        merchantMapper.updateByPrimaryKey(merchant);
    }

    @GlobalTransactional
    public void deleteMerchant(Long id){
        MerchantPo merchant = new MerchantPo();
        merchant.setUseStatus(MerchantStatus.DELETE);
        merchant.setId(id);
        merchantMapper.updateByPrimaryKey(merchant);
    }

    public Object listMerchant(String key, String location,Integer page,Integer size){
        PageHelper.startPage(page,size);
        Example example = new Example(MerchantPo.class);
        Example.Criteria criteria = example.createCriteria();
        if (key!=null){
            criteria.andLike("conpanyName","%"+key+"%");
            criteria.orLike("linkName","%"+key+"%");
        }
        if (location!=null){
            Example.Criteria criteria1 = example.createCriteria();
            criteria.andEqualTo("positionCode",location);
            example.getOredCriteria().add(criteria1);
        }
        Example.Criteria criteria1 = example.createCriteria();
        criteria.andNotEqualTo("useStatus",MerchantStatus.DELETE);
        example.getOredCriteria().add(criteria1);
        List<MerchantPo> merchantPos = merchantMapper.selectByExample(example);
        return new PageInfo<>(merchantPos);
    }

    public Object getMerchantById(Long id){
        return merchantMapper.selectByPrimaryKey(id);
    }

}
