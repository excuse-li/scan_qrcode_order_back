package cn.llq.utils.config;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
* @author lei
* @version 1.0
* @desc feign 设置请求头
* @date 2020-12-04 13:36
*/
@Configuration
public class FeignRequestConfig implements RequestInterceptor {
   @Override
   public void apply(RequestTemplate template) {
       ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
       if (attributes==null){
           return;
       }
       HttpServletRequest request = attributes.getRequest();
       Enumeration<String> headerNames = request.getHeaderNames();
       if (headerNames == null) {
           return;
       }
       //处理上游请求头信息，传递时继续携带
       while (headerNames.hasMoreElements()) {
           String name = headerNames.nextElement();
           String values = request.getHeader(name);
           template.header(name, values);
       }
   }
}