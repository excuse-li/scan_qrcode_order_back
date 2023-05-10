package cn.llq.utils.response;

import lombok.Data;

@Data
public class ResultVO<T> {
    int code = 200;
    String msg = "操作成功";
    T body;

    public ResultVO() {
    }

    public ResultVO(int code, String msg, T body) {
        this.code = code;
        this.msg = msg;
        this.body = body;
    }

    public ResultVO(T data) {
        this(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    public ResultVO(String msg) {
        this(ResultCode.SUCCESS.getCode(), msg, null);
    }

    public ResultVO(String msg, T body) {
        this(ResultCode.SUCCESS.getCode(), msg, body);
    }

    public static ResultVO success(String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.SUCCESS.getCode());
        resultVO.setMsg(msg);
        resultVO.setBody(null);
        return resultVO;
    }

    public static ResultVO success(Object body) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.SUCCESS.getCode());
        resultVO.setBody(body);
        return resultVO;
    }

    public static ResultVO success(String msg, Object body) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.SUCCESS.getCode());
        resultVO.setMsg(msg);
        resultVO.setBody(body);
        return resultVO;
    }

    public static ResultVO fail(String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.VALIDATE_FAILED.getCode());
        resultVO.setMsg(msg);
        resultVO.setBody(null);
        return resultVO;
    }

    public static ResultVO fail(String msg, Object body) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(ResultCode.VALIDATE_FAILED.getCode());
        resultVO.setMsg(msg);
        resultVO.setBody(body);
        return resultVO;
    }

}
