package cn.llq.shop.web;

import cn.llq.shop.model.pojo.MerchantPo;
import cn.llq.shop.service.MerchantService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("merchant")
public class MerchantWeb {

    @Autowired
    MerchantService merchantService;

    @PostMapping
    @HasPermition("admin")
    public Object addMerchant(@RequestBody MerchantPo merchantPo){
        return ResultVO.success(merchantService.addMerchant(merchantPo));
    }

    @PutMapping
    @HasPermition("admin")
    public Object updateMerchant(@RequestBody MerchantPo merchantPo){
        merchantService.updateMerchant(merchantPo);
        return ResultVO.success(null);
    }

    @PutMapping("stop/{id}")
    @HasPermition("admin")
    public Object stopMerchant(@PathVariable("id") Long id){
        merchantService.stopMerchant(id);
        return ResultVO.success(null);
    }
    @PutMapping("start/{id}")
    @HasPermition("admin")
    public Object startMerchant(@PathVariable("id") Long id){
        merchantService.startMerchant(id);
        return ResultVO.success(null);
    }

    @DeleteMapping("{id}")
    @HasPermition("admin")
    public Object deleteMerchant(@PathVariable("id") Long id){
        merchantService.deleteMerchant(id);
        return ResultVO.success(null);
    }

    @GetMapping("list")
    @HasPermition("admin")
    public Object listMerchant(@RequestParam(required = false)String key, @RequestParam(defaultValue = "1")Integer page,
                               @RequestParam(defaultValue = "10")Integer size){
        return ResultVO.success(merchantService.listMerchant(key,null,page,size));
    }

    @GetMapping("{id}")
    @HasPermition("admin")
    public Object getMerchant(@PathVariable("id") Long id){
        return ResultVO.success(merchantService.getMerchantById(id));
    }
}
