package cn.llq.utils.response.exception.handle;

import cn.llq.utils.response.ResultVO;
import cn.llq.utils.response.exception.APIException;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExiceptionHardle {
    @ExceptionHandler(BlockException.class)
    public ResultVO<String> APIExceptionHandler(HttpServletResponse response, BlockException e) {
        // 注意哦，这里返回类型是自定义响应体
        response.setStatus(429);
        return new ResultVO(429, "服务器繁忙", e.getMessage());
    }
}
