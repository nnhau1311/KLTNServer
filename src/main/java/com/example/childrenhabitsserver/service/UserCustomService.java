package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.base.request.BasePageRequest;
import com.example.childrenhabitsserver.common.HostAddress;
import com.example.childrenhabitsserver.common.constant.UserRole;
import com.example.childrenhabitsserver.common.constant.UserStatus;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.request.user.ChangePasswordUserRequest;
import com.example.childrenhabitsserver.common.request.user.CreateNewUserRequest;
import com.example.childrenhabitsserver.common.request.user.ResetPasswordUserRequest;
import com.example.childrenhabitsserver.common.request.user.UpdateUserInforRequest;
import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.repository.UserRepository;
import com.example.childrenhabitsserver.utils.DateTimeUtils;
import com.example.childrenhabitsserver.utils.PageableUtils;
import com.example.childrenhabitsserver.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .createdDate(new Date())
                .updatedDate(new Date())
                .userAddress("Ch??a c???p nh???t")
                .userAddress("Ch??a c???p nh???t")
                .role(UserRole.NORMAL)
                .build();
        UserCustomStorage userCustomStorageDBNew = userRepository.save(userCustomStorage);
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", createNewUserRequest.getUserFullName());
        scopes.put("userName", createNewUserRequest.getUserName());
        NotificationModel notificationModel = NotificationModel.builder()
                .to(createNewUserRequest.getEmail())
                .template("CreateNewUserWithSystem")
                .scopes(scopes)
                .subject("Th??ng b??o t???o t??i kho???n th??nh c??ng")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return userCustomStorageDBNew;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage signUpANewUser(CreateNewUserRequest createNewUserRequest){
        UserCustomStorage userCustomStorageDB = userRepository.findByUsernameOrEmail(createNewUserRequest.getUserName(), createNewUserRequest.getEmail());
        if (userCustomStorageDB != null && userCustomStorageDB.getStatus() == UserStatus.ACTIVE) {
            log.error("T??m th???y ng?????i d??ng ???? t???n t???i: {} {}", userCustomStorageDB.getUsername(), userCustomStorageDB.getEmail());
            throw new ServiceException(ErrorCodeService.USER_HAD_EXITS);
        }
        String passBCrypt = passwordEncoder.encode(createNewUserRequest.getUserPassword());
        UserCustomStorage userCustomStorage = UserCustomStorage.builder()
                .username(createNewUserRequest.getUserName())
                .password(passBCrypt)
                .email(createNewUserRequest.getEmail())
                .role(createNewUserRequest.getRole())
                .userFullName(createNewUserRequest.getUserFullName())
                .status(UserStatus.IN_ACTIVE)
                .expirationJWTDate(new Date())
                .createdDate(new Date())
                .updatedDate(new Date())
                .userAddress("Ch??a c???p nh???t")
                .userNumberPhone("Ch??a c???p nh???t")
                .role(UserRole.NORMAL)
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
                .subject("X??c nh???n t???o t??i kho???n")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return userCustomStorageDBNew;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage confirmCreateNewUser(String userId){
        if (StringUtils.isBlank(userId)) {
            log.error("User ID kh??ng h???p l???:" + userId);
            throw new ServiceException(ErrorCodeService.USER_ID_NOT_VALID);
        }
        Optional<UserCustomStorage> userCustomStorageOptional = userRepository.findById(userId);
        if (!userCustomStorageOptional.isPresent()) {
            log.error("Kh??ng t??m th???y ng?????i d??ng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = userCustomStorageOptional.get();
        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedDate(new Date());
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage changePassword(String userId, ChangePasswordUserRequest request){

        if (StringUtils.isBlank(userId)) {
            log.error("User ID kh??ng h???p l???:" + userId);
            throw new ServiceException(ErrorCodeService.USER_ID_NOT_VALID);
        }
        Optional<UserCustomStorage> UserCustomStorageOptional = userRepository.findById(userId);
        if (!UserCustomStorageOptional.isPresent()) {
            log.error("Kh??ng t??m th???y ng?????i d??ng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = UserCustomStorageOptional.get();
        Boolean isMatchPassword = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (isMatchPassword) {
            log.info("New pass: {}", request.getNewPassword());
            String passBCrypt = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(passBCrypt);
            user.setUpdatedDate(new Date());
        }
        UserCustomStorage result = userRepository.save(user);
        // G???i email
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", user.getUserFullName());
        scopes.put("userName", user.getUsername());
        NotificationModel notificationModel = NotificationModel.builder()
                .to(user.getEmail())
                .template("NotifyChangePasswordUser")
                .scopes(scopes)
                .subject("Th??ng b??o thay ?????i m???t kh???u ng?????i d??ng")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return result;
    }

    public String sendEmailToConfirmResetPassword(ResetPasswordUserRequest request){
        if (StringUtils.isBlank(request.getUserInfor())) {
            log.error("Request null ho???c r???ng:" + request.getUserInfor());
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        // Ki???m tra xem user c?? t???n t???i trong database kh??ng?
        UserCustomStorage user = userRepository.findByUsernameOrEmail(request.getUserInfor(), request.getUserInfor());
        if (user == null) {
            log.error("Kh??ng t??m th???y ng?????i d??ng:" + request.getUserInfor());
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
                .subject("X??c nh???n reset m???t kh???u t??i kho???n")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return "???? g???i email!";
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage resetPassword(String userId){
        if (StringUtils.isBlank(userId)) {
            log.error("User ID kh??ng h???p l???:" + userId);
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        Optional<UserCustomStorage> UserCustomStorageOptional = userRepository.findById(userId);
        if (!UserCustomStorageOptional.isPresent()) {
            log.error("Kh??ng t??m th???y ng?????i d??ng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = UserCustomStorageOptional.get();
        String randomResetPassword = randomUtils.randomArrayStringWithOnlyLetter();
        log.info("New pass: {}", randomResetPassword);
        String passBCrypt = passwordEncoder.encode(randomResetPassword);
        user.setPassword(passBCrypt);
        user.setUpdatedDate(new Date());


        UserCustomStorage result = userRepository.save(user);

        // G???i email
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", user.getUserFullName());
        scopes.put("userName", user.getUsername());
        scopes.put("newPassword", randomResetPassword);
        NotificationModel notificationModel = NotificationModel.builder()
                .to(user.getEmail())
                .template("NotifyResetPasswordUser")
                .scopes(scopes)
                .subject("Th??ng b??o reset m???t kh???u ng?????i d??ng")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage changePassword(ResetPasswordUserRequest resetPasswordUserRequest){
        if (StringUtils.isBlank(resetPasswordUserRequest.getUserInfor())) {
            log.error("User ID kh??ng h???p l???:" + resetPasswordUserRequest.getUserInfor());
            throw new ServiceException(ErrorCodeService.REQUEST_RESET_PASSWORD_INVALID);
        }
        UserCustomStorage user = userRepository.findByUsernameOrEmail(resetPasswordUserRequest.getUserInfor(), resetPasswordUserRequest.getUserInfor());
        if (user == null) {
            log.error("Kh??ng t??m th???y ng?????i d??ng:" + resetPasswordUserRequest.getUserInfor());
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        String oldPass = resetPasswordUserRequest.getOldPassword();
        String newPass = resetPasswordUserRequest.getNewPassword();
        String passBCrypt = "";
        if (passwordEncoder.matches(oldPass, user.getPassword())) {
            passBCrypt = passwordEncoder.encode(newPass);
            user.setPassword(passBCrypt);
            user.setUpdatedDate(new Date());
        }


        UserCustomStorage customStorage = userRepository.save(user);
        // G???i email
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", user.getUserFullName());
        scopes.put("userName", user.getUsername());
        NotificationModel notificationModel = NotificationModel.builder()
                .to(user.getEmail())
                .template("NotifyChangePasswordUser")
                .scopes(scopes)
                .subject("Th??ng b??o thay ?????i m???t kh???u ng?????i d??ng")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return customStorage;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage updateExpirationJWTDate(String userId, Date expirationJWTDate){
        UserCustomStorage userCustomStorage = findById(userId);
        userCustomStorage.setExpirationJWTDate(expirationJWTDate);
        userCustomStorage.setUpdatedDate(new Date());
        userCustomStorage.setAccessDate(new Date());
        return userRepository.save(userCustomStorage);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage disableUser(String userId){
        UserCustomStorage userCustomStorage = findById(userId);
        if (userCustomStorage.getUsername() == "admin") {
            throw new ServiceException(ErrorCodeService.USER_IS_ROOT_ADMIN);
        }
        userCustomStorage.setUpdatedDate(new Date());
        userCustomStorage.setStatus(UserStatus.DISABLE);
        return userRepository.save(userCustomStorage);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage activeUser(String userId){
        UserCustomStorage userCustomStorage = findById(userId);
        userCustomStorage.setUpdatedDate(new Date());
        userCustomStorage.setStatus(UserStatus.ACTIVE);
        return userRepository.save(userCustomStorage);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserCustomStorage updateUserInfor(String userId, UpdateUserInforRequest updateUserInforRequest){
        UserCustomStorage userCustomStorage = findById(userId);
        userCustomStorage.setUserFullName(updateUserInforRequest.getUserFullName());
        userCustomStorage.setUserAddress(updateUserInforRequest.getUserAddress());
        userCustomStorage.setUserNumberPhone(updateUserInforRequest.getUserNumberPhone());
        return userRepository.save(userCustomStorage);
    }

    // Query Data ====================================================

    public Page<UserCustomStorage> findAll(BasePageRequest request) {
        Pageable pageable = PageableUtils.convertPageableAndSort(request.getPageNumber(), 10, new ArrayList<>());
        Page<UserCustomStorage> result = userRepository.findAll(pageable);
        return result;
    }

    public List<UserCustomStorage> findAll() {
        List<UserCustomStorage> result = userRepository.findAll();
        return result;
    }

    public UserCustomStorage findById(String userId) {
        Optional<UserCustomStorage> userCustomStorageOptional = userRepository.findById(userId);
        if (!userCustomStorageOptional.isPresent()) {
            log.error("Kh??ng t??m th???y ng?????i d??ng:" + userId);
            throw new ServiceException(ErrorCodeService.USER_HAD_NOT_EXITS);
        }
        UserCustomStorage user = userCustomStorageOptional.get();
        return user;
    }
}
