package cn.llq.goods.web;

import cn.llq.good.menuList.bo.GoodsBo;
import cn.llq.good.menuList.pojo.GoodsType;
import cn.llq.good.menuList.pojo.StoreGoodsDown;
import cn.llq.goods.service.GoodsService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsWeb {

    @Autowired
    GoodsService goodsService;

    @PostMapping("type")
    @HasPermition("goods")
    public Object addGoodsType(@RequestBody GoodsType goodsType, @RequestHeader("authorization") String token){
        goodsService.addGoodsType(goodsType, token);
        return ResultVO.success("成功");
    }
    @PutMapping("type")
    @HasPermition("goods")
    public Object updateGoodsType(@RequestBody GoodsType goodsType, @RequestHeader("authorization") String token){
        goodsService.updateGoodsType(goodsType, token);
        return ResultVO.success("成功");
    }

    @DeleteMapping("type/{id}")
    @HasPermition("goods")
    public Object deleteType(@PathVariable("id")Long id){
        goodsService.deleteType(id);
        return ResultVO.success("成功");
    }

    @GetMapping("type/list")
    @HasPermition("goods")
    public Object listGoodsType(@RequestHeader("authorization") String token){
        return ResultVO.success("成功",goodsService.listGoodsType(token));
    }

    @PostMapping("goods")
    @HasPermition("goods")
    public Object addGoods(@RequestBody GoodsBo goodsBo, @RequestHeader("authorization") String token){
        goodsService.addGoods(goodsBo, token);
        return ResultVO.success("成功");
    }

    @PutMapping("goods")
    @HasPermition("goods")
    public Object updateGoods(@RequestBody GoodsBo goodsBo, @RequestHeader("authorization") String token){
        goodsService.updateGoods(goodsBo, token);
        return ResultVO.success("成功");
    }

    @DeleteMapping("goods/{id}")
    @HasPermition("goods")
    public Object deleteGoods(@PathVariable("id")String id){
        goodsService.deleteGoods(id);
        return ResultVO.success("成功");
    }


    @PutMapping("start/{id}")
    @HasPermition("goods")
    public Object startGoods(@PathVariable("id")String id){
        goodsService.startGoods(id);
        return ResultVO.success("成功");
    }

    @PutMapping("stop/{id}")
    @HasPermition("goods")
    public Object stopGoods(@PathVariable("id")String id){
        goodsService.stopGoods(id);
        return ResultVO.success("成功");
    }

    @GetMapping("/goods/{id}")
    public Object getGoodsById(@PathVariable("id")String id){
        return ResultVO.success("成功",goodsService.getGoodsById(id));
    }

    @GetMapping("listByStore/{id}")
    public Object listByStore(@PathVariable("id")Long id,@RequestParam(value = "showDown",defaultValue = "0")Integer showDown){
        return ResultVO.success("成功",goodsService.listByStore(id,showDown));
    }

    @GetMapping("listByMenuList/{id}")
    public Object listMenuList(@PathVariable("id")Long id,@RequestParam(value = "showDown",defaultValue = "0")Integer showDown){
        return ResultVO.success("成功",goodsService.listByMenuList(id,showDown));
    }

    @PostMapping("store/goods")
    @HasPermition("goods")
    public Object downGoodsStore(@RequestBody StoreGoodsDown storeGoodsDown){
        goodsService.downGoodsStore(storeGoodsDown);
        return ResultVO.success("成功");
    }

    @PutMapping("store/goods")
    @HasPermition("goods")
    public Object upGoodsStore(@RequestBody StoreGoodsDown storeGoodsDown){
        goodsService.upGoodsStore(storeGoodsDown);
        return ResultVO.success("成功");
    }

    @GetMapping("store/goods/down/{id}")
    @HasPermition("goods")
    public Object listDownGoodsStore(@PathVariable("id")Long id){
        return ResultVO.success("成功", goodsService.listDownGoodsStore(id));
    }

    @GetMapping("type/goods/{id}")
    public Object listByType(@PathVariable("id") Long id,@RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "size",defaultValue = "10")Integer size,@RequestParam(value = "name",defaultValue = "") String name){
        return ResultVO.success("成功", goodsService.listByType(id,name,page,size));
    }

    @GetMapping("goods/sku/{id}")
    public Object getBySkuId(@PathVariable("id") String id){
        return ResultVO.success("成功", goodsService.getBySkuId(id));
    }
}
