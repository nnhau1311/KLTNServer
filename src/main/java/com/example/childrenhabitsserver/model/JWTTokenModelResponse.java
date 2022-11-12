package com.example.childrenhabitsserver.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JWTTokenModelResponse {
    private String jwtToken;
    private String userId;
    private Date expirationJWTDate;
}
