package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.common.request.user.CreateNewUserRequest;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.user.ResetPasswordUserRequest;
import com.example.childrenhabitsserver.service.UserCustomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    private final UserCustomService customUserDetailsService;

    public UserController(UserCustomService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public WrapResponse<Object> createANewUser(@Valid @RequestBody CreateNewUserRequest createNewUserRequest) {
        // Tạo 1 user không cần check
        return WrapResponse.ok(customUserDetailsService.createANewUser(createNewUserRequest));
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public WrapResponse<Object> signUpANewUser(@Valid @RequestBody CreateNewUserRequest createNewUserRequest) {
        // Kiểm tra 1 số điều kiện cơ bản và xác nhận khi tạo user
        return WrapResponse.ok(customUserDetailsService.signUpANewUser(createNewUserRequest));
    }

    @RequestMapping(value = "/confirm-create-new/{userId}", method = RequestMethod.POST)
    public WrapResponse<Object> confirmSignUpANewUser(@PathVariable String userId) {
        // Kiểm tra 1 số điều kiện cơ bản và xác nhận khi tạo user
        return WrapResponse.ok(customUserDetailsService.confirmCreateNewUser(userId));
    }

    @RequestMapping(value = "/request-reset-password", method = RequestMethod.POST)
    public WrapResponse<Object> requestResetPasswordUser(@Valid @RequestBody ResetPasswordUserRequest resetPasswordUserRequest) {
        // Thực hiện yêu cầu reset password, hệ thống kiểm tra và gửi email xác nhận
        return WrapResponse.ok(customUserDetailsService.sendEmailToConfirmResetPassword(resetPasswordUserRequest));
    }

    @RequestMapping(value = "/confirm-reset-password/{userId}", method = RequestMethod.GET)
    public WrapResponse<Object> requestResetPasswordUser(@PathVariable String userId) {
        // Đường linh xác nhân reset mật khẩu
        log.info("Reset password id {}", userId);
        return WrapResponse.ok(customUserDetailsService.resetPassword(userId));
    }
}
