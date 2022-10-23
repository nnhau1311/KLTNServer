package com.example.childrenhabitsserver.common.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewUserRequest {
    private String userName; // Tên đăng nhập
    private String userFullName; // Tên người dùng
    private String userPassword;
    private String email;
    private String role;
}
