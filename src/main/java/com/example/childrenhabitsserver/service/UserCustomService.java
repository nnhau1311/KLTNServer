package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.common.HostAddress;
import com.example.childrenhabitsserver.common.UserStatus;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.request.user.ChangePasswordUserRequest;
import com.example.childrenhabitsserver.common.request.user.CreateNewUserRequest;
import com.example.childrenhabitsserver.common.request.user.ResetPasswordUserRequest;
import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.repository.UserRepository;
import com.example.childrenhabitsserver.utils.DateTimeUtils;
import com.example.childrenhabitsserver.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public UserCustomStorage createANewUser(CreateNewUserRequest createNewUserRequest){
        String passBCrypt = passwordEncoder.encode(createNewUserRequest.getUserPassword());
        UserCustomStorage userCustomStorage = UserCustomStorage.builder()
                .username(createNewUserRequest.getUserName())
                .password(passBCrypt)
                .email(createNewUserRequest.getEmail())
                .role(createNewUserRequest.getRole())
                .userFullName(createNewUserRequest.getUserFullName())
                .status(UserStatus.ACTIVE)
                .expirationJWTDate(DateTimeUtils.addDate(new Date(), 12))
                .createDate(new Date())
                .updateDate(new Date())
                .build();
        UserCustomStorage userCustomStorageDBNew = userRepository.save(userCustomStorage);
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", createNewUserRequest.getUserFullName());
        scopes.put("userName", createNewUserRequest.getUserName());
        NotificationModel notificationModel = NotificationModel.builder()
                .to(createNewUserRequest.getEmail())
                .template("CreateNewUserWithSystem")
                .scopes(scopes)
                .subject("Thông báo tạo tài khoản thành công")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return userCustomStorageDBNew;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage signUpANewUser(CreateNewUserRequest createNewUserRequest){
        UserCustomStorage userCustomStorageDB = userRepository.findByUsernameOrEmail(createNewUserRequest.getUserName(), createNewUserRequest.getEmail());
        if (userCustomStorageDB != null && userCustomStorageDB.getStatus() == UserStatus.ACTIVE) {
            log.error("Tìm thấy người dùng đã tồn tại: {} {}", userCustomStorageDB.getUsername(), userCustomStorageDB.getEmail());
            throw new ServiceException(ErrorCodeService.USER_HAD_EXITS);
        }
        String passBCrypt = passwordEncoder.encode(createNewUserRequest.getUserPassword());
        UserCustomStorage userCustomStorage = UserCustomStorage.builder()
                .username(createNewUserRequest.getUserName())
                .password(passBCrypt)
                .email(createNewUserRequest.getEmail())
                .role(createNewUserRequest.getRole())
                .userFullName(createNewUserRequest.getUserFullName())
                .status(UserStatus.DISABLE)
                .expirationJWTDate(new Date())
                .createDate(new Date())
                .updateDate(new Date())
                .build();
        UserCustomStorage userCustomStorageDBNew = userRepository.save(userCustomStorage);
        String apiConfirmCreateUser = String.format("http://%s%s%s", HostAddress.serverAddress, "/user/confirm-create-new/", userCustomStorageDBNew.getId());
        log.info("apiConfirmCreateUser {}", apiConfirmCreateUser);
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", createNewUserRequest.getUserFullName());
        scopes.put("userName", createNewUserRequest.getUserName());
        scopes.put("apiConfirmCreateUser", apiConfirmCreateUser);
        NotificationModel notificationModel = NotificationModel.builder()
                .to(createNewUserRequest.getEmail())
                .template("ConfirmCreateNewUser")
                .scopes(scopes)
                .subject("Xác nhận tạo tài khoản")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return userCustomStorageDBNew;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage confirmCreateNewUser(String userId){
        if (StringUtils.isBlank(userId)) {
            log.error("User ID không hợp lệ:" + userId);
            throw new ServiceException(ErrorCodeService.USER_ID_NOT_VALID);
        }
        Optional<UserCustomStorage> userCustomStorageOptional = userRepository.findById(userId);
        if (!userCustomStorageOptional.isPresent()) {
            log.error("Không tìm thấy người dùng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = userCustomStorageOptional.get();
        user.setStatus(UserStatus.ACTIVE);
        user.setUpdateDate(new Date());
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage changePassword(String userId, ChangePasswordUserRequest request){

        if (StringUtils.isBlank(userId)) {
            log.error("User ID không hợp lệ:" + userId);
            throw new ServiceException(ErrorCodeService.USER_ID_NOT_VALID);
        }
        Optional<UserCustomStorage> UserCustomStorageOptional = userRepository.findById(userId);
        if (!UserCustomStorageOptional.isPresent()) {
            log.error("Không tìm thấy người dùng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = UserCustomStorageOptional.get();
        Boolean isMatchPassword = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (isMatchPassword) {
            log.info("New pass: {}", request.getNewPassword());
            String passBCrypt = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(passBCrypt);
            user.setUpdateDate(new Date());
        }
        return userRepository.save(user);
    }

    public String sendEmailToConfirmResetPassword(ResetPasswordUserRequest request){
        if (StringUtils.isBlank(request.getUserInfor())) {
            log.error("Request null hoặc rỗng:" + request.getUserInfor());
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        // Kiểm tra xem user có tồn tại trong database không?
        UserCustomStorage user = userRepository.findByUsernameOrEmail(request.getUserInfor(), request.getUserInfor());
        if (user == null) {
            log.error("Không tìm thấy người dùng:" + request.getUserInfor());
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
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
    public UserCustomStorage resetPassword(String userId){
        if (StringUtils.isBlank(userId)) {
            log.error("User ID không hợp lệ:" + userId);
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        Optional<UserCustomStorage> UserCustomStorageOptional = userRepository.findById(userId);
        if (!UserCustomStorageOptional.isPresent()) {
            log.error("Không tìm thấy người dùng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = UserCustomStorageOptional.get();
        String randomResetPassword = randomUtils.randomArrayStringWithOnlyLetter();
        log.info("New pass: {}", randomResetPassword);
        String passBCrypt = passwordEncoder.encode(randomResetPassword);
        user.setPassword(passBCrypt);
        user.setUpdateDate(new Date());

        // Gửi email
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

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage updateExpirationJWTDate(String userId, Date expirationJWTDate){
        UserCustomStorage userCustomStorage = findById(userId);
        userCustomStorage.setExpirationJWTDate(expirationJWTDate);
        userCustomStorage.setUpdateDate(new Date());
        return userRepository.save(userCustomStorage);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage disableUser(String userId){
        UserCustomStorage userCustomStorage = findById(userId);
        userCustomStorage.setUpdateDate(new Date());
        userCustomStorage.setStatus(UserStatus.DISABLE);
        return userRepository.save(userCustomStorage);
    }

    // Query Data ====================================================

    public UserCustomStorage findById(String userId) {
        Optional<UserCustomStorage> userCustomStorageOptional = userRepository.findById(userId);
        if (!userCustomStorageOptional.isPresent()) {
            log.error("Không tìm thấy người dùng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = userCustomStorageOptional.get();
        return user;
    }
}
