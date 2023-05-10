package cn.llq.file.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class AliyunOssConfig {
    @Value("key")
    String key;

    @Value("secret")
    String secret;

    @Value("entrypoint")
    String entryPoint;

    @Value("bucket")
    String bucket;
}
