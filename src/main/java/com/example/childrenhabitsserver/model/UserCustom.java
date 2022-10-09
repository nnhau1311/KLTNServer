package com.example.childrenhabitsserver.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.Column;
import java.util.Collection;

//@Entity
//@Table(name = "userCustom")
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class UserCustom extends User {

//    @Id
//    @GeneratedValue(generator="system-uuid")
//    @GenericGenerator(name="system-uuid", strategy = "uuid")
//    @Column(name = "userId")
    private String id;

    private String sessionId;
    @Column(name = "userNameCustom",nullable = false, unique = true)
    private String username;
    @Column(name = "userPasswordCustom")
    private String password;
    private String role;

    public UserCustom(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserCustom(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
