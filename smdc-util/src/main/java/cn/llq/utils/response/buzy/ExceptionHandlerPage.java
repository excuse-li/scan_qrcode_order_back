package cn.llq.utils.response.buzy;

import cn.llq.utils.response.ResultVO;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
 
/**
 * 自定义sentinel异常返回信息
 */
@Component
public class ExceptionHandlerPage implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
        // BlockException 异常接口，其子类为Sentinel五种规则异常的实现类
        // AuthorityException 授权异常
        // DegradeException 降级异常
        // FlowException 限流异常
        // ParamFlowException 参数限流异常
        // SystemBlockException 系统负载异常
        ResultVO resultVO = new ResultVO<>();
        if (e instanceof FlowException || e instanceof DegradeException) {
            httpServletResponse.setStatus(429);
            resultVO.setCode(429);
            resultVO.setMsg("服务器繁忙");
            resultVO.setMsg(e.getMessage());
        }

        e.printStackTrace();
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(resultVO));
    }
}