package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.AccessDeniedException;
import com.example.childrenhabitsserver.common.UserStatus;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.entity.CustomUserDetails;
import com.example.childrenhabitsserver.entity.UserCustomStorge;
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
        UserCustomStorge user = userRepository.findByUsernameOrEmail(userInfor, userInfor);
        log.info("user: {}", user.toString());
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            log.error("Not found user with username:" + userInfor);
            log.error("User status:" + user.getStatus());
//            throw new UsernameNotFoundException(userInfor);
            throw new AccessDeniedException(ErrorCodeService.LOGIN_INVALID);
        }
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(String id){
        Optional<UserCustomStorge> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            UserCustomStorge user = userOptional.get();
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new IndexOutOfBoundsException("User with id: "+id+" is inactive");
            }
            return new CustomUserDetails(user);
        }else {
            throw new IndexOutOfBoundsException("Not found user with id: "+id);
        }

    }
}
