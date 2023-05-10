package cn.llq.shop.web;

import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.service.ShopStoreService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShopStoreWeb {

    @Autowired
    ShopStoreService shopStoreService;

    @GetMapping("list")
    @HasPermition("store")
    public Object getList(ShopStorePo shopStorePo, @RequestParam(value = "page",required = false)Integer page,
                          @RequestParam(value = "size",required = false) Integer size,@RequestHeader("authorization") String authorization) throws Exception {
        if (page==null||page<0){
            page =1;
        }
        if (size==null||size<0){
            size =10;
        }
        return ResultVO.success(shopStoreService.listPageByShopStore(shopStorePo,page,size,authorization));
    }

    @GetMapping("/{id}")
    public Object getInfoById(@PathVariable("id") Long id){
        return ResultVO.success(shopStoreService.getShopStoreById(id));
    }

    @PostMapping("")
    @HasPermition("store")
    public Object addShopStore(@RequestBody ShopStorePo shopStorePo,@RequestHeader("authorization") String token){
        shopStoreService.addShopStore(shopStorePo,token);
        return ResultVO.success("成功");
    }

    @PutMapping("stop/{id}")
    @HasPermition("store")
    public Object stopShopStore(@PathVariable("id") Long id){
        shopStoreService.stopShopStore(id);
        return ResultVO.success("成功");
    }
    @PutMapping("start/{id}")
    @HasPermition("store")
    public Object startShopStore(@PathVariable("id") Long id){
        shopStoreService.startShopStore(id);
        return ResultVO.success("成功");
    }

    @PutMapping("")
    public Object updateShopStore(@RequestBody ShopStorePo shopStorePo){
        shopStoreService.updateShopStore(shopStorePo);
        return ResultVO.success("成功");
    }

    @DeleteMapping("{id}")
    @HasPermition("store")
    public Object deleteShopStore(@PathVariable("id") Long id){
        shopStoreService.deleteShopStore(id);
        return ResultVO.success("成功");
    }
}
