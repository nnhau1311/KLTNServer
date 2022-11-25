package com.example.childrenhabitsserver.common.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordUserRequest {
    private String userInfor; // Tên đăng nhập hoặc email
    private String oldPassword; // Mật khẩu cũ
    private String newPassword; // Mật khẩu cũ
}
