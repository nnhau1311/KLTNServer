package com.example.childrenhabitsserver.base.exception;

import com.example.childrenhabitsserver.base.BaseException;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(String errorCode, Object... args) {
        super(errorCode, args);
    }

    public static BaseException build(String errorCode, String msgDetail) {
        AccessDeniedException exp = new AccessDeniedException(errorCode);
        exp.setMessage(msgDetail);
        return exp;
    }
}
