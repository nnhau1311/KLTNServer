package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.common.request.CreateNewUserRequest;
import com.example.childrenhabitsserver.common.request.LoginRequest;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.service.UserCustomService;
import com.example.childrenhabitsserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserCustomService customUserDetailsService;

    public UserController(UserCustomService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public WrapResponse<Object> authenticateUser(@Valid @RequestBody CreateNewUserRequest createNewUserRequest) {
        return WrapResponse.ok(customUserDetailsService.createANewUser(createNewUserRequest));
    }
}
