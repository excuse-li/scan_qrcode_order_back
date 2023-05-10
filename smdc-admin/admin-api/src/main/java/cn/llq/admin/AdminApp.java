package cn.llq.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.request.RequestContextListener;
import tk.mybatis.spring.annotation.MapperScan;

@SpringCloudApplication
@RefreshScope
@EnableDiscoveryClient
@MapperScan("cn.llq.admin.dao")
@ComponentScan(basePackages={"cn.llq"})
@EnableFeignClients
public class AdminApp {
    public static void main(String[] args) {
        SpringApplication.run(AdminApp.class);
    }
    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
