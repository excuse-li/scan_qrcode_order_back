package cn.llq.utils.response.exception;

import cn.llq.utils.response.ResultCode;
import lombok.Data;

@Data
public class APIException extends RuntimeException {
    private int code;
    private String msg;

    public APIException() {
        this(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getMsg());
    }

    public APIException(String msg) {
        this(ResultCode.VALIDATE_FAILED.getCode(), msg);
    }

    public APIException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}
