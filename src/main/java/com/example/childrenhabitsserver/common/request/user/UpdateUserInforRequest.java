package com.example.childrenhabitsserver.common.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserInforRequest {
//    private String email;
    private String userFullName; // tên người dùng
    private String userAddress;
    private String userNumberPhone;
}
