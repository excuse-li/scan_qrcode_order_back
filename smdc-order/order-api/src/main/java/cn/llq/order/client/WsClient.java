package cn.llq.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ws-api")
public interface WsClient {
    @PostMapping("addGood")
    Object addGoods(@RequestParam("tableId") Long tableId,@RequestParam("storeId") Long storeId);
    @PostMapping("emitOrder")
    Object emitOrder(@RequestParam("tableId") Long tableId,@RequestParam("storeId") Long storeId);
}
