package cn.llq.member.service;

import cn.llq.member.dao.MemberMapper;
import cn.llq.member.pojo.Member;
import cn.llq.utils.RedisUtil;
import cn.llq.utils.response.exception.APIException;
import com.alibaba.fastjson.JSONObject;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class MemberService {

    @Autowired
    MemberMapper memberMapper;
    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    RedisUtil redisUtil;
    @Value("${wx.appId}")
    String appId;c
    @Value("${wx.appsecret}")
    String appsecret;cc


    @GlobalTransactional
    public void save(Member member) {
        String openId = member.getOpenId();

        Member member1 = new Member();
        member1.setOpenId(openId);
        List<Member> select = memberMapper.select(member1);
        if (select.size()>0){
            member.setId(select.get(0).getId());
            memberMapper.updateByPrimaryKeySelective(member);
        }else{
            member.setId(UUID.randomUUID().toString());
            memberMapper.insertSelective(member);
        }
    }

    public Object getUserByOpenId(String openId){
        Member member1 = new Member();
        member1.setOpenId(openId);

        return memberMapper.selectOne(member1);
    }

    @GlobalTransactional
    public Object getLoginByCode(String code){

        String str = restTemplate.getForObject("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appsecret + "&code=" + code + "&grant_type=authorization_code", String.class);
        System.out.println(str);
        JSONObject object =(JSONObject) JSONObject.parse(str);
        if ("40163".equals(object.getString("errcode"))){
            throw new APIException(400, "code已失效");
        }
        String openid = (String)object.get("openid");
        Object user = this.getUserByOpenId(openid);
        String s = UUID.randomUUID().toString();
        if(user!=null){
            redisUtil.set(s,user,24*60*60*1000);
            return s;
        }
        String  str1 = restTemplate.getForObject("https://api.weixin.qq.com/sns/userinfo?access_token=" + object.get("access_token") + "&openid=" + openid + "&lang=zh_CN", String.class);
        JSONObject userInfo= (JSONObject)JSONObject.parse(str1);
        Member member = new Member();
        member.setId(UUID.randomUUID().toString());
        member.setOpenId(openid);
        try {
            member.setCity(new String(userInfo.get("city").toString().getBytes("ISO-8859-1"),"utf-8"));
            member.setCountry(new String(userInfo.get("country").toString().getBytes("ISO-8859-1"),"utf-8"));
            member.setHeadimg(new String(userInfo.get("headimgurl").toString().getBytes("ISO-8859-1"),"utf-8"));
            member.setProvince(new String(userInfo.get("province").toString().getBytes("ISO-8859-1"),"utf-8"));
            member.setNickname(new String(userInfo.get("nickname").toString().getBytes("ISO-8859-1"),"utf-8"));
            member.setUnionid(userInfo.get("unionid")!=null?userInfo.get("unionid").toString():null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.save(member);

        redisUtil.set(s,member,24*60*60*1000);
        return s;
    }

    public String getAccessToken(){
        return redisUtil.get("accessToken").toString();
    }

    public Object getUserInfo(String token){
        return redisUtil.get(token);
    }

    public Object getUsersById(List<String> list){
        Example example = new Example(Member.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",list);
        return memberMapper.selectByExample(criteria);
    }
}
