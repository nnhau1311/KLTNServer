package com.example.childrenhabitsserver.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String accessToken;
    private String tokenType;
}
