package cn.llq.utils.response.exception.handle;


import cn.llq.utils.response.ResultVO;
import cn.llq.utils.response.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(APIException.class)
    public ResultVO<String> APIExceptionHandler(HttpServletResponse response,APIException e) {
        // 注意哦，这里返回类型是自定义响应体
        response.setStatus(e.getCode());
        return new ResultVO(e.getCode(), e.getMsg(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<String> MethodArgumentNotValidExceptionHandler(HttpServletResponse response,MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        // 注意哦，这里返回类型是自定义响应体
        response.setStatus(400);
        return new ResultVO<>(400, "参数校验失败", objectError.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultVO<String> exceptionHandler(HttpServletResponse response,Exception e) {
        // ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        // 注意哦，这里返回类型是自定义响应体
        if (e instanceof NoHandlerFoundException){
            return new ResultVO(404,"请求不存在", null);
        }
        response.setStatus(500);
        e.printStackTrace();
        return new ResultVO(500,"服务器出现异常了，请联系网络管理员", null);
    }
}
