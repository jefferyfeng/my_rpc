package com.fdh;

public enum  ResultEnum {
    SUCCESS(1,"成功"),
    EXCEPTION(9,"其他异常！");
    public int code;
    public String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
