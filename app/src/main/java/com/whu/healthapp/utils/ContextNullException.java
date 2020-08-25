package com.whu.healthapp.utils;


/**
 * Created by 47462 on 2016/10/25.
 */
public class ContextNullException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ContextNullException() {
        this("Context is not Null");
    }

    public ContextNullException(String detailMessage) {
        this(detailMessage,null);
    }

    public ContextNullException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ContextNullException(Throwable throwable) {
        this("Context is not Null",throwable);
    }
}
