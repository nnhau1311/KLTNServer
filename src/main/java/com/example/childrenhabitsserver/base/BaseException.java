package com.example.childrenhabitsserver.base;
import com.example.childrenhabitsserver.utils.MessageSourceUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {

    protected String errorCode;

    protected String message;

    public BaseException() {
    }

    protected BaseException(String errorCode, Object... args) {
        this.errorCode = errorCode;
        this.message = MessageSourceUtils.getMessage(errorCode, args);
    }
}
