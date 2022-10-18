package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.common.request.CreateNewUserRequest;
import com.example.childrenhabitsserver.entity.UserCustomStorge;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserCustomService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendEmailNotificationService sendEmailNotificationService;

    public UserCustomService(UserRepository userRepository, PasswordEncoder passwordEncoder, SendEmailNotificationService sendEmailNotificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sendEmailNotificationService = sendEmailNotificationService;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorge createANewUser(CreateNewUserRequest createNewUserRequest){
        String passBCrypt = passwordEncoder.encode(createNewUserRequest.getUserName());
        UserCustomStorge userCustomStorge = UserCustomStorge.builder()
                .username(createNewUserRequest.getUserName())
                .password(passBCrypt)
                .email(createNewUserRequest.getEmail())
                .role(createNewUserRequest.getRole())
                .userFullName(createNewUserRequest.getUserFullName())
                .build();
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", createNewUserRequest.getUserFullName());
        scopes.put("userName", createNewUserRequest.getUserName());
        NotificationModel notificationModel = NotificationModel.builder()
                .to(createNewUserRequest.getEmail())
                .template("ConfirmCreateNewUser")
                .scopes(scopes)
                .subject("Xác nhận tạo tài khoản")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return userRepository.save(userCustomStorge);
    }
}
