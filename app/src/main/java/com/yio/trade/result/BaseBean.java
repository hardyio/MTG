package com.yio.trade.result;

public class BaseBean<T> {

    private int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
