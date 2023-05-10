package cn.llq.member;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;
import tk.mybatis.spring.annotation.MapperScan;

@SpringCloudApplication
@RefreshScope
@EnableDiscoveryClient
@MapperScan("cn.llq.member.dao")
@ComponentScan(basePackages={"cn.llq"})
@EnableFeignClients
public class MemberApp {
    public static void main(String[] args) {
        SpringApplication.run(MemberApp.class);
    }
    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
//    @Bean
//    public RestTemplate restTemplate(){
//        return new RestTemplate();
//    }
}
