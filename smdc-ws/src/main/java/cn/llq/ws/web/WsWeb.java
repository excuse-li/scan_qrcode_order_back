package cn.llq.ws.web;

import cn.llq.utils.response.ResultVO;
import cn.llq.ws.ws.WsHandle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class WsWeb {

    @PostMapping("addGood")
    public Object addGoods(@RequestParam("tableId") Long tableId,@RequestParam("storeId") Long storeId){
        WsHandle.addGoods(tableId,storeId);
        return ResultVO.success("成功");
    }

    @PostMapping("emitOrder")
    public Object emitOrder(@RequestParam("tableId") Long tableId,@RequestParam("storeId") Long storeId){
        WsHandle.emitOrder(tableId,storeId);
        return ResultVO.success("成功");
    }
}
