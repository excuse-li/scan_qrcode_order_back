package cn.llq.goods.web;

import cn.llq.good.menuList.pojo.MenuList;
import cn.llq.good.menuList.pojo.MenuListGoods;
import cn.llq.good.menuList.pojo.StoreMenu;
import cn.llq.goods.service.MenuListService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("menuList")
public class MenuListWeb {

    @Autowired
    MenuListService menuListService;

    @PostMapping
    @HasPermition("menuList")
    public Object addMenuList(@RequestBody MenuList menuList, @RequestHeader("authorization") String authorization){
        menuListService.addMenuList(menuList, authorization);
        return ResultVO.success(null);
    }

    @PutMapping
    @HasPermition("menuList")
    public Object updateMenuList(@RequestBody MenuList menuList){
        menuListService.updateMenuList(menuList);
        return ResultVO.success(null);
    }

    @PutMapping("/stop")
    @HasPermition("menuList")
    public Object stopMenuList(@RequestBody MenuList menuList){
        menuListService.stopMenuList(menuList.getId());
        return ResultVO.success(null);
    }

    @PutMapping("/start")
    @HasPermition("menuList")
    public Object startMenuList(@RequestBody MenuList menuList){
        menuListService.startMenuList(menuList.getId());
        return ResultVO.success(null);
    }

    @DeleteMapping("{id}")
    @HasPermition("menuList")
    public Object deleteMenuList(@PathVariable("id") Long id){
        menuListService.deleteMenuList(id);
        return ResultVO.success(null);
    }

    @GetMapping("{id}")
    public Object getMenuListById(@PathVariable("id") Long id){
        return ResultVO.success(menuListService.getMenuListById(id));
    }

    @GetMapping("store/{id}")
    public Object getMenuByStoreId(@PathVariable("id") Long id){
        return ResultVO.success(menuListService.getStoreMenu(id));
    }

    @GetMapping("list")
    @HasPermition("menuList")
    public Object listMenuList(@RequestParam(value = "page",defaultValue = "1")Integer page,@RequestParam(value = "size",defaultValue = "10")Integer size,@RequestHeader("authorization") String authorization,@RequestParam(value = "name",defaultValue = "")String name){
        return ResultVO.success(menuListService.listMenuList(name,page,size,authorization));
    }
    @PostMapping("/goods")
    @HasPermition("menuList")
    public Object addGoods(@RequestBody List<MenuListGoods> menuListGoods){
        menuListService.addGoods(menuListGoods);
        return ResultVO.success("成功");
    }

    @PutMapping("/goods")
    @HasPermition("menuList")
    public Object deleteGoods(@RequestBody MenuListGoods menuListGoods){
        menuListService.deleteGoods(menuListGoods);
        return ResultVO.success("成功");
    }

    @PutMapping("store")
    @HasPermition("menuList")
    public Object updateMenuList(@RequestBody StoreMenu storeMenu){
        menuListService.updateMenuList(storeMenu);
        return ResultVO.success("成功");
    }
}
