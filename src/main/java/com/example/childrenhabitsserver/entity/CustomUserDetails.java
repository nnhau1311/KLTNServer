package com.example.childrenhabitsserver.entity;

import com.example.childrenhabitsserver.common.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private UserCustomStorge user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
//        Date currentDate = new Date();
//        if (user.getExpirationJWTDate() != null && user.getExpirationJWTDate().before(currentDate)){
//            return true;
//        }
//        return false;
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (user.getStatus().equals(UserStatus.ACTIVE)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        Date currentDate = new Date();
        if (user.getExpirationJWTDate() == null) {
            return true;
        }
        if (user.getExpirationJWTDate().before(currentDate)){
            return true;
        }

        return false;
    }

    @Override
    public boolean isEnabled() {
        if (user.getStatus().equals(UserStatus.ACTIVE)){
            return true;
        }
        return false;
    }
}
