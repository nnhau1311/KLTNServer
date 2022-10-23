package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.entity.CustomUserDetails;
import com.example.childrenhabitsserver.entity.UserCustomStorge;
import com.example.childrenhabitsserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String userInfor) throws UsernameNotFoundException {
        // Kiểm tra xem user có tồn tại trong database không?
        UserCustomStorge user = userRepository.findByUsernameOrEmail(userInfor, userInfor);

        if (user == null) {
            log.error("Not found user with username:" + userInfor);
            throw new UsernameNotFoundException(userInfor);
        }
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(String id){
        Optional<UserCustomStorge> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            UserCustomStorge user = userOptional.get();
            return new CustomUserDetails(user);
        }else {
            throw new IndexOutOfBoundsException("Not found user with id: "+id);
        }

    }
}