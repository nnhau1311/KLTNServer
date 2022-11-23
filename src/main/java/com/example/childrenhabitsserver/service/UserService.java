package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.AccessDeniedException;
import com.example.childrenhabitsserver.common.constant.UserStatus;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.entity.CustomUserDetails;
import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String userInfor) throws UsernameNotFoundException {
        // Kiểm tra xem user có tồn tại trong database không?
        UserCustomStorage user = userRepository.findByUsernameOrEmail(userInfor, userInfor);
        if (user == null) {
            log.error("User null");
            throw new AccessDeniedException(ErrorCodeService.LOGIN_INVALID);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.error("Not found user with username:" + userInfor);
            log.error("User status:" + user.getStatus());
            throw new AccessDeniedException(ErrorCodeService.LOGIN_INVALID);
        }
        log.info("user: {}", user.toString());
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(String id){
        Optional<UserCustomStorage> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            UserCustomStorage user = userOptional.get();
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new AccessDeniedException("User with id: "+id+" is inactive");
            }
            return new CustomUserDetails(user);
        } else {
            throw new AccessDeniedException("User with id: "+id+" is inactive");
        }

    }
}
