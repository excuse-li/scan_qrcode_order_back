package cn.llq.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.seata.common.util.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public class JWTUtil {
    public static String generateToken(String username, String salt, Long expireSecond) {
        final Logger log=LoggerFactory.getLogger(JWTUtil.class);
        try {
            if (StringUtils.isBlank(username)) {
                log.error("username不能为空");
                return null;
            }
            log.debug("username:{}", username);

            // 如果盐值为空，则使用默认值：666666
            if (StringUtils.isBlank(salt)) {
                salt = "666666";
            }

            // 过期时间，单位：秒
            // 默认过期时间为1小时 3600L 单位秒
            if (expireSecond == null) {
                expireSecond = 24*3600L;
            }
            Date expireDate = DateUtils.addSeconds(new Date(), expireSecond.intValue());

            // 生成token
            Algorithm algorithm = Algorithm.HMAC256(salt);
            String token = JWT.create()
                    .withClaim("smdc", username)
                    // jwt唯一id
                    .withJWTId(UUID.randomUUID().toString())
                    // 签发人
                    .withIssuer("")
                    // 主题
                    .withSubject("")
                    // 签发的目标
                    .withAudience("")
                    // 签名时间
                    .withIssuedAt(new Date())
                    // token过期时间
                    .withExpiresAt(expireDate)
                    // 签名
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            log.error("generateToken exception", e);
        }
        return null;
    }
}
