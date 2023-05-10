package cn.llq.shop;

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
@MapperScan("cn.llq.shop.dao")
@ComponentScan(basePackages={"cn.llq"})
@EnableFeignClients
public class ShopApp {
    public static void main(String[] args) {
//        SimpleHttpHeartbeatSender
//        SpringApplication.run(ShopApp.class);
        int i= 1;
        while (i<100){
            i++;
            System.out.println( i++);
        }
    }
    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
