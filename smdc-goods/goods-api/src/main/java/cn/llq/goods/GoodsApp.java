package cn.llq.goods;

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
@MapperScan("cn.llq.goods.dao")
@ComponentScan(basePackages={"cn.llq"})
@EnableFeignClients
public class GoodsApp {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApp.class);
    }
    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
