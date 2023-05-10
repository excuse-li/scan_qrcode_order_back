package cn.llq.order.client;

import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.utils.response.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("shop-api")
@RequestMapping("table")
public interface TableClient {
    @PutMapping
    ResultVO updateTable(@RequestBody StoreTable storeTable);

    @GetMapping("{id}")
    ResultVO<StoreTable> getTableById(@PathVariable("id")Long id);
}
