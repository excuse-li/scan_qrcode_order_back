package cn.llq.member.web;

import cn.llq.member.pojo.Member;
import cn.llq.member.service.MemberService;
import cn.llq.member.util.Scheduled;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("member")
@Lazy
public class MemberWeb {

    @Autowired
    MemberService memberService;




    @PostMapping("/{code}")
    public Object getTokenByCode(@PathVariable("code")String code){
        Object loginByCode = memberService.getLoginByCode(code);
        return ResultVO.success("成功",loginByCode);
    }

    @GetMapping("")
    public Object getUserInfoByToken(@RequestHeader("token")String token){
        return ResultVO.success("成功", memberService.getUserInfo(token));
    }

    @PostMapping("list")
    public Object getUserInfoByList(@RequestBody List<String> userIds){
        return ResultVO.success("成功", memberService.getUsersById(userIds));
    }
    @GetMapping("accessToken")
    public Object getAccessToken(){
        return ResultVO.success("成功", memberService.getAccessToken());
    }

}
