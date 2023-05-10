package cn.llq.utils.response;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),

    FAILED(501, "响应失败"),

    VALIDATE_FAILED(400, "参数校验失败"),

    LOGIN_ERROR(401, "登录失效"),

    NO_PERMITION(403, "不允许"),

    ERROR(500, "未知错误");

    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
