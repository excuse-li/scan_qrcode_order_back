package cn.llq.order.web;

import cn.llq.order.service.CountService;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("count")
public class CountWeb {
    @Autowired
    CountService countService;

    @GetMapping("card")
    public Object getCardData(@RequestHeader("authorization") String token){
        return ResultVO.success("成功", countService.getCardCount(token));
    }

    @GetMapping("pie")
    public Object getPieData(@RequestHeader("authorization") String token){
        return ResultVO.success("成功", countService.getPieData(token));
    }

    @GetMapping("line")
    public Object getLineData(@RequestHeader("authorization") String token){
        return ResultVO.success("成功", countService.getLineData(token));
    }

    @GetMapping("balance")
    public Object getBalance(){
        return ResultVO.success("成功", countService.getBlanceInfo());
    }

}
