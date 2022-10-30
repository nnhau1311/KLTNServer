package com.example.childrenhabitsserver.common.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordUserRequest {
    private String oldPassword;
    private String newPassword;
}
