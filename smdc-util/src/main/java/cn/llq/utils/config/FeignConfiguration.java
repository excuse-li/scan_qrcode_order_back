//package cn.llq.utils.config;
//
//import feign.RequestInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Enumeration;

//@Configuration
//public class FeignConfiguration {
//    @Bean
//    protected RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            if (attrs != null) {
//                HttpServletRequest request = attrs.getRequest();
//                Enumeration<String> headerNames = request.getHeaderNames();
//                if (headerNames != null) {
//                    while (headerNames.hasMoreElements()) {
//                        String name = headerNames.nextElement();
//                        String value = request.getHeader(name);
//                        /**
//                         * 遍历请求头里面的属性字段，将logId和token添加到新的请求头中转发到下游服务
//                         * */
//                        requestTemplate.header(name, value);
//                    }
//                }
//            }
//        };
//    }
//}