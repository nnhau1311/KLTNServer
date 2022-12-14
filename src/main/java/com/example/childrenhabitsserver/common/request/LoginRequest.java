package com.example.childrenhabitsserver.common.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull(message = "Username mustn't empty")
    private String username;
    @NotNull(message = "Password mustn't empty")
    private String password;
}
