package cn.llq.order.web;

import cn.llq.order.service.OrderService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;


@RestController
@RequestMapping("order")
public class OrderWeb {


    @Autowired
    OrderService orderService;

//    @GetMapping("testPrint/{id}")
//    public Object testPrint(@PathVariable("id")String id){
//        orderService.printOrder(id);
//        return ResultVO.success("成功",null);
//    }

    /**
     * 创建订单
     * @return
     */
    @PostMapping("{storeId}/{tableId}")
    public Object createOrder(@PathVariable("storeId")Long storeId,@PathVariable("tableId")Long tableId){
        orderService.createOrder( tableId,storeId);
        return ResultVO.success("成功",null);
    }

    /**
     * 根据桌id获取订单
     * @return
     */
    @GetMapping("table/{tableId}")
    public Object getByTableId(@PathVariable("tableId")Long tableId){
        return ResultVO.success("成功",orderService.getByTableId(tableId));
    }

    /**
     * 根据订单id获取详情
     * @return
     */
    @GetMapping("/{id}")
    public Object getById(@PathVariable("id")String id){

        return ResultVO.success("成功",orderService.getById(id));
    }

    /**
     * 获取订单详情
     * @return
     */
    @GetMapping("list")
    public Object list(@RequestHeader("authorization")String token,@RequestParam(value = "orderNo",defaultValue = "")String orderNo,@RequestParam(value = "storeId",defaultValue = "0") Long storeId,
                       @RequestParam(value = "startTime",defaultValue = "1970-01-01 00:00:00") String startTime,@RequestParam(value = "endTime",defaultValue = "3099-01-01 00:00:00") String endTime
            ,@RequestParam(value = "page",defaultValue = "1")Integer page,@RequestParam(value = "size",defaultValue = "10")Integer size){

        return ResultVO.success("成功", orderService.getOrderListByParam(token,orderNo,storeId,startTime,endTime,page,size));
    }

    /**
     * 用户获取订单列表
     * @return
     */
    @GetMapping("user/list")
    public Object userList(@RequestParam(value = "page",defaultValue = "1")Integer page,@RequestParam(value = "size",defaultValue = "10")Integer size){
        return ResultVO.success("成功", orderService.userList(page,size));
    }

    /**
     * 支付订单
     * @return
     */
    @PutMapping("pay/{id}")
    public Object payment(@PathVariable("id")String id,@RequestHeader("token")String token){

        return ResultVO.success(orderService.generaPayOrder(id,token));
    }

    /**
     * 部分商品退货
     * @return
     */
    @PutMapping("rejected/goods")
    @HasPermition("order")
    public Object rejectedGood(@RequestParam("orderGoodsId")String orderGoodsId,@RequestParam("num")Integer num){
        // TODO
        orderService.rejectedGood(orderGoodsId,num);
        return ResultVO.success("成功",null );
    }

    /**
     * 用户删除订单
     * @return
     */
    @DeleteMapping("/{id}")
    public Object userDelete(){
        // TODO
        return null;
    }

    @PostMapping("generaPayOrder/{orderId}")
    public Object generaPayOrder(@PathVariable("orderId")String orderId,@RequestHeader("token")String token) throws Exception {
        return ResultVO.success("成功",orderService.generaPayOrder(orderId,token) );
    }

    @PostMapping("result")
    public Object payResult(@RequestBody String result) throws Exception {
        return orderService.payResult(result);
    }

    @PutMapping("offLinePay")
    public Object offLinePay(@RequestBody Map map){
        Object orderId = map.get("orderId");
        Object amount = map.get("amount");
        Object payType = map.get("payType");
        return ResultVO.success("成功",orderService.offLinePay(orderId.toString(),new BigDecimal(amount.toString()),Integer.valueOf(payType.toString())) );
    }
}
