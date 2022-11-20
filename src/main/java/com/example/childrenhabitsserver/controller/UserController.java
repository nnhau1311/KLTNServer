package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.auth.JwtTokenProvider;
import com.example.childrenhabitsserver.common.request.user.ChangePasswordUserRequest;
import com.example.childrenhabitsserver.common.request.user.CreateNewUserRequest;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.user.ResetPasswordUserRequest;
import com.example.childrenhabitsserver.common.request.user.UpdateUserInforRequest;
import com.example.childrenhabitsserver.service.UserCustomService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    private final UserCustomService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserCustomService customUserDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
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

    @RequestMapping(value = "/confirm-create-new/{userId}", method = RequestMethod.GET)
    public WrapResponse<Object> confirmSignUpANewUser(@PathVariable String userId) {
        return WrapResponse.ok(customUserDetailsService.confirmCreateNewUser(userId));
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public WrapResponse<Object> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @Valid @RequestBody ChangePasswordUserRequest changePasswordUserRequest) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(customUserDetailsService.changePassword(userId, changePasswordUserRequest));
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

    @RequestMapping(value = "/disable/{userId}", method = RequestMethod.GET)
    public WrapResponse<Object> disableUser(@PathVariable String userId) {
        log.info("disable userId {}", userId);
        return WrapResponse.ok(customUserDetailsService.disableUser(userId));
    }

    @ApiOperation(value = "Cập nhật thông tin cá nhân của người dùng", notes = "Truyền id qua path")
    @RequestMapping(value = "/update-infor", method = RequestMethod.POST)
    public WrapResponse<Object> updateInfor(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody UpdateUserInforRequest request) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        log.info("update userId {}", userId);
        return WrapResponse.ok(customUserDetailsService.updateUserInfor(userId, request));
    }

    @ApiOperation(value = "Cập nhật thông tin cá nhân của người dùng", notes = "Truyền id qua path")
    @RequestMapping(value = "/update-infor/{userId}", method = RequestMethod.POST)
    public WrapResponse<Object> updateInforWithUserId(@PathVariable String userId, @RequestBody UpdateUserInforRequest request) {
        log.info("update userId {}", userId);
        return WrapResponse.ok(customUserDetailsService.updateUserInfor(userId, request));
    }

    // QUERY ====================================================
    @RequestMapping(value = "/find-by-id/{userId}", method = RequestMethod.GET)
    public WrapResponse<Object> findById(@PathVariable String userId) {
        return WrapResponse.ok(customUserDetailsService.findById(userId));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public WrapResponse<Object> getUserInfor(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(customUserDetailsService.findById(userId));
    }
}
