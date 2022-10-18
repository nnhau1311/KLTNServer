package com.example.childrenhabitsserver.base.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Violation {
    private String field;
    private String errorCode;
    private String message;
}
