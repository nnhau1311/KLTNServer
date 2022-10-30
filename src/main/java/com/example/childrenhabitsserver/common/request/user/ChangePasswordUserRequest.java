package com.example.childrenhabitsserver.common.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordUserRequest {
    private String userId;
    private String oldPassword;
    private String newPassword;
}
