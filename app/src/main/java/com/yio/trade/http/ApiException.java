package com.yio.trade.http;

public class ApiException extends RuntimeException{

    private int errorCode;

    public ApiException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
