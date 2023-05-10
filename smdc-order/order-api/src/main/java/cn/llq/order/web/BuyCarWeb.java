package cn.llq.order.web;


import cn.llq.order.service.BuyCarService;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("buyCar")
public class BuyCarWeb {

    @Autowired
    BuyCarService buyCarService;

    /**
     * 加入餐桌
     * @return
     */
    @PostMapping("join/{tableId}")
    public Object join(@RequestHeader(value = "token",required = false)String token,@PathVariable("tableId")Long tableId){
        buyCarService.joinBuyCar(tableId,token);
        return ResultVO.success("成功",null);
    }

    /**
     * 添加商品
     * @return
     */
    @PostMapping("addGoods/{tableId}/{skuId}/{num}")
    public Object addGoods(@PathVariable("tableId")Long tableId,@PathVariable("num")Integer num,@PathVariable("skuId")String skuId ){
        buyCarService.addGoods(tableId,skuId,num);
        return ResultVO.success("成功",null);
    }

    /**
     * 清除购物车
     * @return
     */
    @DeleteMapping("{tableId}")
    public Object deleteBuyCarByTableId(@PathVariable("tableId")Long tableId){
        buyCarService.deleteBuyCar(tableId);
        return ResultVO.success("成功",null);
    }

    @GetMapping("{tableId}")
    public Object getBuyCar(@PathVariable("tableId")Long tableId){
        buyCarService.getBuyCarInfo(tableId);
        return ResultVO.success("成功", buyCarService.getBuyCarInfo(tableId));
    }
}
