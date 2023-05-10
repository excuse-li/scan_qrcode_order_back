package cn.llq.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringCloudApplication
@RefreshScope
@EnableDiscoveryClient
@ComponentScan(basePackages={"cn.llq"})
@EnableFeignClients
public class WsApp {
    public static void main(String[] args) {
        SpringApplication.run(WsApp.class);
    }
}
