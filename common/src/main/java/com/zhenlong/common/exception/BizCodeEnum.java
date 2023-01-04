package com.zhenlong.common.exception;

public enum BizCodeEnum {
    VALID_EXCEPTION(10001, "parameter format validation failed"),
    UNKNOWN_EXCEPTION(10000, "System unknown error")
    ;

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
