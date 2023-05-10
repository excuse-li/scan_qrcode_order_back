package cn.llq.order.client;

import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.utils.response.ResultVO;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@FeignClient("shop-api")
public interface StoreClient {
    @GetMapping("list")
    ResultVO<PageInfo<ShopStorePo>> getStoreList( @RequestParam(value = "page",required = false)Integer page,
                                                        @RequestParam(value = "size",required = false) Integer size,@RequestHeader("authorization") String authorization);

    @PostMapping("print/printMsg")
    Object printMsg(@RequestBody HashMap<String,Object> map);

    @GetMapping("/{id}")
    ResultVO<ShopStorePo> getShopStoreById(@PathVariable("id") Long id);

    @GetMapping("table/{id}")
    ResultVO<StoreTable> getTableById(@PathVariable("id") Long id);

    @GetMapping("user/info")
    ResultVO<Object> getUserInfo();
}
