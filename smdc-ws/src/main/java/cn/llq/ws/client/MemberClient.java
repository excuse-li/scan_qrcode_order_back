package cn.llq.ws.client;

import cn.llq.member.pojo.Member;
import cn.llq.utils.response.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("member-api")
@RequestMapping("member")
public interface MemberClient {
    @GetMapping("")
    ResultVO<Member> getUserInfoByToken(@RequestHeader("token") String token);
}
