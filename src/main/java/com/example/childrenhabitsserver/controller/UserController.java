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
        return WrapResponse.ok(customUserDetailsService.createANewUser(createNewUserRequest));
    }

    @RequestMapping(value = "/request-reset-password", method = RequestMethod.POST)
    public WrapResponse<Object> requestResetPasswordUser(@Valid @RequestBody ResetPasswordUserRequest resetPasswordUserRequest) {
        return WrapResponse.ok(customUserDetailsService.sendEmailToConfirmResetPassword(resetPasswordUserRequest));
    }

    @RequestMapping(value = "/confirm-reset-password/{userId}", method = RequestMethod.GET)
    public WrapResponse<Object> requestResetPasswordUser(@PathVariable String userId) {
        log.info("Reset password id {}", userId);
        return WrapResponse.ok(customUserDetailsService.resetPassword(userId));
    }
}
