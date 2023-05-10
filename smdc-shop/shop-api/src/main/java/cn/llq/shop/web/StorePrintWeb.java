package cn.llq.shop.web;


import cn.llq.shop.model.pojo.StorePrint;
import cn.llq.shop.service.StorePrintService;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/print")
public class StorePrintWeb {
    @Autowired
    StorePrintService storePrintService;

    @PostMapping("")
    public Object addPrint(@RequestBody StorePrint storePrint){
        storePrintService.addPrint(storePrint);
        return ResultVO.success("成功",null);
    }

    @DeleteMapping("/{id}")
    public Object deletePrint(@PathVariable("id")Long id){
        storePrintService.deletePrint(id);
        return ResultVO.success("成功",null);
    }

    @GetMapping("/")
    public Object getPrintList(@RequestParam(value = "printName",defaultValue = "")String printName,@RequestParam(value = "storeId",defaultValue = "0")Long storeId,
                               @RequestParam(value = "page",defaultValue = "1")Integer page,@RequestParam(value = "size",defaultValue = "10")Integer size,@RequestHeader("authorization")String token){
        return ResultVO.success("成功", storePrintService.getPrintList(printName,storeId,page,size,token));
    }

    @PostMapping("printMsg")
    public Object printMsg(@RequestBody HashMap<String,Object> map){
        storePrintService.printInfo(Long.valueOf(map.get("storeId").toString()),map.get("msg").toString());
        return ResultVO.success("成功",null);
    }
}
