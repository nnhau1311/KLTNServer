package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.common.HostAddress;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.request.user.CreateNewUserRequest;
import com.example.childrenhabitsserver.common.request.user.ResetPasswordUserRequest;
import com.example.childrenhabitsserver.entity.UserCustomStorge;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.repository.UserRepository;
import com.example.childrenhabitsserver.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class UserCustomService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final RandomUtils randomUtils;

    public UserCustomService(UserRepository userRepository, PasswordEncoder passwordEncoder, SendEmailNotificationService sendEmailNotificationService, RandomUtils randomUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sendEmailNotificationService = sendEmailNotificationService;
        this.randomUtils = randomUtils;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorge createANewUser(CreateNewUserRequest createNewUserRequest){
        String passBCrypt = passwordEncoder.encode(createNewUserRequest.getUserPassword());
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
    public String sendEmailToConfirmResetPassword(ResetPasswordUserRequest request){
        if (StringUtils.isBlank(request.getUserInfor())) {
            log.error("Request null hoặc rỗng:" + request.getUserInfor());
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        // Kiểm tra xem user có tồn tại trong database không?
        UserCustomStorge user = userRepository.findByUsernameOrEmail(request.getUserInfor(), request.getUserInfor());
        if (user == null) {
            log.error("Không tìm thấy người dùng:" + request.getUserInfor());
            throw new UsernameNotFoundException(request.getUserInfor());
        }
        String apiAddressConfirm = String.format("http://%s%s%s", HostAddress.serverAddress, "/user/confirm-reset-password/", user.getId());
        log.info("apiAddressConfirm {}", apiAddressConfirm);
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", user.getUserFullName());
        scopes.put("apiConfirmResetPassword", apiAddressConfirm);
        NotificationModel notificationModel = NotificationModel.builder()
                .to(user.getEmail())
                .template("ConfirmResetPasswordUser")
                .scopes(scopes)
                .subject("Xác nhận reset mật khẩu tài khoản")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return "Đã gửi email!";
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorge resetPassword(String userId){
        if (StringUtils.isBlank(userId)) {
            log.error("User ID không hợp lệ:" + userId);
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        Optional<UserCustomStorge> userCustomStorgeOptional = userRepository.findById(userId);
        if (!userCustomStorgeOptional.isPresent()) {
            log.error("Không tìm thấy người dùng:" + userId);
            throw new UsernameNotFoundException(userId);
        }
        UserCustomStorge user = userCustomStorgeOptional.get();
        String randomResetPassword = randomUtils.randomArrayStringWithOnlyLetter();
        log.info("New pass: {}", randomResetPassword);
        String passBCrypt = passwordEncoder.encode(randomResetPassword);
        user.setPassword(passBCrypt);
//        UserCustomStorge userCustomStorge = UserCustomStorge.builder()
//                .username(createNewUserRequest.getUserName())
//                .password(passBCrypt)
//                .email(createNewUserRequest.getEmail())
//                .role(createNewUserRequest.getRole())
//                .userFullName(createNewUserRequest.getUserFullName())
//                .build();
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", user.getUserFullName());
        scopes.put("userName", user.getUsername());
        scopes.put("newPassword", randomResetPassword);
        NotificationModel notificationModel = NotificationModel.builder()
                .to(user.getEmail())
                .template("NotifyResetPasswordUser")
                .scopes(scopes)
                .subject("Thông báo reset mật khẩu người dùng")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return userRepository.save(user);
    }
}
