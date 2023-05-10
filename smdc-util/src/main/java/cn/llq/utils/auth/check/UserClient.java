package cn.llq.utils.auth.check;


import cn.llq.utils.response.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("shop-service")
@Component
public interface UserClient {
    @GetMapping("1")
    ResultVO get();

}
