package cn.llq.shop.web;

import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.shop.service.TableService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("table")
public class TableWeb {

    @Autowired
    TableService tableService;

    @PostMapping
    @HasPermition("table")
    public Object addTable(@RequestBody StoreTable storeTable){
        tableService.addTable(storeTable);
        return ResultVO.success("添加成功");
    }

    @PutMapping
//    @HasPermition("table")
    public Object updateTable(@RequestBody StoreTable storeTable){
        tableService.updateTable(storeTable);
        return ResultVO.success("修改成功");
    }

    @GetMapping("{id}")
    public Object getTableById(@PathVariable("id") Long id){
        return ResultVO.success(tableService.getTableById(id));
    }

    @DeleteMapping("{id}")
    @HasPermition("table")
    public Object deleteTable(@PathVariable("id") Long id){
        tableService.deleteTable(id);
        return ResultVO.success("删除成功");
    }

    @GetMapping("list")
    @HasPermition("table")
    public Object getTableList(@RequestParam(value = "name",defaultValue = "") String name,@RequestParam(value = "page",defaultValue = "1") Integer page,
                               @RequestParam(value = "size",defaultValue = "10")Integer size,@RequestParam(value = "storeId",defaultValue = "0") Long storeId,
                               @RequestHeader("authorization") String token){
        return ResultVO.success(tableService.getTableList(name,page,size,storeId,token));
    }

    @GetMapping("qrCode/{id}")
    public Object getQrCode(@PathVariable("id")Long id){
        return ResultVO.success("成功",tableService.getQrCode(id));
    }
}
