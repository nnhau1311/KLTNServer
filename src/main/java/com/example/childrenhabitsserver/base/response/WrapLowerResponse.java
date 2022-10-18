package com.example.childrenhabitsserver.base.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class WrapLowerResponse<T> {

    private boolean success;
    private T data;
    private String errorCode;
    private List<String> message;
    private String refCode;
    private String statusCode;

    public static <T> WrapLowerResponse<T> error(String msg) {
        WrapLowerResponse baseResponse = new WrapLowerResponse();
        baseResponse.setData(null);
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("500");
        baseResponse.setMessage(Collections.singletonList(msg));
        return baseResponse;
    }

    public static <T> WrapLowerResponse<T> ok(T data) {
        WrapLowerResponse baseResponse = new WrapLowerResponse();
        baseResponse.setData(data);
        baseResponse.setSuccess(true);
        baseResponse.setStatusCode("200");
        return baseResponse;
    }

    public static <T> CompletableFuture<WrapLowerResponse<T>> okFuture(CompletableFuture<T> data) {
        return data.thenApply(rs -> WrapLowerResponse.ok(rs));
    }
}
