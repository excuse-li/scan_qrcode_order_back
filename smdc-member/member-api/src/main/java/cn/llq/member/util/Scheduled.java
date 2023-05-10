package cn.llq.member.util;

import cn.llq.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
@EnableScheduling
public class Scheduled {

    @Autowired
    RedisUtil redisUtil;
    @Value("${wx.appId}")
    String appId;
    @Value("${wx.appsecret}")
    String appsecret;



    @org.springframework.scheduling.annotation.Scheduled(cron = "0 33 */1 * * ?")
    public void getAccessToken(){

        System.out.println("定时获取token");
        RestTemplate restTemplate=new RestTemplate();
        HashMap body = restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appsecret, HashMap.class);

        System.out.println(body);
        System.out.println("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appsecret);
        if (body.get("access_token") != null) {
            redisUtil.set("accessToken",body.get("access_token"));
            System.out.println("定时获取token成功");
        }
    }
}
