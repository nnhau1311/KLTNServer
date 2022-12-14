package com.example.childrenhabitsserver.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "userCustom")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserCustomStorage {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "userId")
    private String id;

    private String sessionId;
    @Column(name = "userNameCustom",nullable = false, unique = true)
    private String username; // tên đăng nhập
    @Column(name = "userPasswordCustom")
    private String password;
    private Integer status;
    private Date expirationJWTDate;
    private Date createdDate;
    private Date updatedDate;
    private Date accessDate;

    private String role;
    private String email;
    private String userFullName; // tên người dùng
    private String userAddress;
    private String userNumberPhone;
}
