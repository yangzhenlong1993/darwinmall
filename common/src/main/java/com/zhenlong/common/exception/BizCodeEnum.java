package com.zhenlong.common.exception;

public enum BizCodeEnum {
    VALID_EXCEPTION(10001, "parameter format validation failed"),
    UNKNOWN_EXCEPTION(10000, "System unknown error"),
    USER_EXIST_EXCEPTION(15001,"user already existed"),
    PHONE_EXIST_EXCEPTION(15002,"phone number already existed"),
    LOGIN_FAILED_EXCEPTION(15003,"username or password is incorrect"),
    PRODUCT_PUT_ON_SALE_EXCEPTION(11000,"put product on sale error");

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
