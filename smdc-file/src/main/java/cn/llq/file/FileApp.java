package cn.llq.file;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringCloudApplication
@ComponentScan(basePackages = {"cn.llq"})
public class FileApp {
    public static void main(String[] args) {
        SpringApplication.run(FileApp.class);
    }
}
