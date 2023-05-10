package cn.llq.shop.web;

import cn.llq.shop.model.bo.UserInfoLoginBo;
import cn.llq.shop.model.pojo.UserInfoPo;
import cn.llq.shop.model.pojo.UserRole;
import cn.llq.shop.service.UserInfoService;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("user")
public class UserWeb {

    @Autowired
    UserInfoService userInfoService;
    @PostMapping("login")
    public Object login(@RequestBody UserInfoLoginBo userInfoLoginBo){
        return ResultVO.success(userInfoService.login(userInfoLoginBo));
    }

    @PostMapping()
    @HasPermition("user")
    public Object addUser(@RequestBody UserInfoPo userInfoPo,@RequestHeader("authorization") String authorization){
        userInfoPo.setJoinTime(new Date());
        userInfoService.addUser(userInfoPo, authorization);
        return ResultVO.success(null);
    }

    @PutMapping
    @HasPermition("user")
    public Object updateUser(@RequestBody UserInfoPo userInfoPo){
        userInfoService.updateUser(userInfoPo);
        return ResultVO.success(null);
    }

    @DeleteMapping("{id}")
    @HasPermition("user")
    public Object deleteUser(@PathVariable("id")Long id){
        userInfoService.deleteUserById(id);
        return ResultVO.success(null);
    }

    @GetMapping("/loginOut")
    public Object loginOut(HttpServletRequest request){
        userInfoService.loginOut(request.getHeader("authorization"));
        return ResultVO.success(null);
    }

    @GetMapping("list/{storeId}")
    @HasPermition("user")
    public Object getUserList(@PathVariable("storeId")Long storeId,@RequestParam(value = "userName",required = false) String userName,
                              @RequestParam(value = "page",defaultValue = "1")Integer page,
                              @RequestParam(value = "size",defaultValue = "10") Integer size,
                              HttpServletRequest request){
        if (page<1){
            page=1;
        }
        if (size<1){
            size=10;
        }
        return ResultVO.success(userInfoService.listUser(storeId,userName,page,size,request.getHeader("authorization")));
    }

    @GetMapping("/{id}")
    @HasPermition("user")
    public Object getUserById(@PathVariable("id")Long id){
        return ResultVO.success(userInfoService.getUserById(id));
    }


    @GetMapping("/info")
    public Object getUserInfo(@RequestHeader("authorization") String token){
        return ResultVO.success(userInfoService.getUserInfo(token));
    }

    @PutMapping("role")
    @HasPermition("user")
    public Object setRole(@RequestBody UserRole userRole){
        return ResultVO.success(userInfoService.setRole(userRole));
    }

    @GetMapping("role/{id}")
    public Object getUserRole(@PathVariable("id")Long id){
        return ResultVO.success(userInfoService.getUserRole(id));
    }

    @GetMapping("role")
    @HasPermition("user")
    public Object getRoleList(){
        return ResultVO.success(userInfoService.getRoleList());
    }

    @GetMapping("checkAuth/{auth}")
    public Object checkAuth(@PathVariable("auth") String auth,@RequestHeader("authorization") String authorization){
        return ResultVO.success(userInfoService.checkAuth(auth,authorization));
    }

}
