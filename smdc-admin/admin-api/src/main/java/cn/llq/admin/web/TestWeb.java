package cn.llq.admin.web;

import cn.llq.utils.auth.HasPermition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestWeb {
    @GetMapping
//    @HasPermition("a")
    public Object a(){
        return "";
    }
}
