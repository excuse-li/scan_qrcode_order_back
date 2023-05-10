package cn.llq.utils.auth;

import cn.llq.utils.response.ResultVO;
import cn.llq.utils.response.exception.APIException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

@Component
@Aspect
//@ComponentScan("cn.llq.utils.auth.check")
public class AuthAspect {
    final Logger logger = LoggerFactory.getLogger(AuthAspect.class);

    @Autowired
    RestTemplate restTemplate;
    @Before("@annotation(HasPermition)")
    public void checkPermition(JoinPoint point){



        MethodSignature signature = (MethodSignature) point.getSignature();
        HasPermition annotation = signature.getMethod().getAnnotation(HasPermition.class);
        String value = annotation.value();
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request =(HttpServletRequest) attributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        String authorization = request.getHeader("authorization");

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setContentType("text/html; charset=UTF-8");
        logger.info("authorization:={}",authorization);
        if (StringUtils.isEmpty(authorization)){
            throw new APIException(501,"请先登录");
        }
        String s = "http://shop-api/user/checkAuth/" + value;
        ResponseEntity<ResultVO> forObject=null;
        try{
            forObject = restTemplate.getForEntity(s,ResultVO.class);
        }catch (HttpStatusCodeException e){
            JSONObject o = (JSONObject)JSONObject.parse(e.getResponseBodyAsString());
            throw new APIException(o.getInteger("code"),o.getString("msg"));
        }

        if (!((Boolean) forObject.getBody().getBody())){
            throw new APIException(403,"权限不足");
        }
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new JsonMimeInterceptor()));
        return restTemplate;
    }

    /**
     * 解决调用时请求头无法传递的问题
     */
    class JsonMimeInterceptor implements ClientHttpRequestInterceptor {


        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            headers.add("Accept", String.valueOf(MediaType.APPLICATION_JSON));
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request1 =(HttpServletRequest) attributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            Enumeration<String> headerNames = request1.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value1 = request1.getHeader(key);
                headers.add(key, value1);
            }
            return execution.execute(request, body);
        }
    }
}
