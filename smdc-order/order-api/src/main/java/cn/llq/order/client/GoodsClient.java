package cn.llq.order.client;

import cn.llq.good.menuList.bo.GoodsBo;
import cn.llq.utils.response.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("goods-api")
public interface GoodsClient {

    @GetMapping("goods/goods/sku/{id}")
    ResultVO<GoodsBo> getBySkuId(@PathVariable("id")String id);
}
