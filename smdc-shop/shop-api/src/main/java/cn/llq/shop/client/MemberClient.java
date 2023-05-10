package cn.llq.shop.client;

import cn.llq.utils.response.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("member-api")
public interface MemberClient {
    @GetMapping("member/accessToken")
    ResultVO<String> getAccessToken();
}
