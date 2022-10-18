package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.BaseException;

public class ServiceException extends BaseException {
    public ServiceException(String errorCode, Object... args) {
        super(errorCode, args);
    }

    public static BaseException build(String errorCode, String msgDetail) {
        ServiceException exp = new ServiceException(errorCode);
        exp.setMessage(msgDetail);
        return exp;
    }
}
