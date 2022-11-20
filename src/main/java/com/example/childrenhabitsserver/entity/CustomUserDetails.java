package com.example.childrenhabitsserver.entity;

import com.example.childrenhabitsserver.common.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@AllArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails {
    private UserCustomStorage user;

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
//        Date currentDate = new Date();
//        if (user.getExpirationJWTDate() == null) {
//            log.info("ExpirationJWTDate {}", user.getExpirationJWTDate());
//            return false;
//        }
//        if (user.getExpirationJWTDate().before(currentDate)){
//            log.info("ExpirationJWTDate {}", user.getExpirationJWTDate());
//            log.info("currentDate {}", currentDate);
//            return false;
//        }

//        return true;
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
