package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.auth.JwtTokenProvider;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.service.UserHabitsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-habits")
@Slf4j
public class UserHabitsController {

    private final UserHabitsService userHabitsService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserHabitsController(UserHabitsService userHabitsService, JwtTokenProvider jwtTokenProvider) {
        this.userHabitsService = userHabitsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @RequestMapping(value = "/create-a-new", method = RequestMethod.POST)
    public UserHabitsStorage saveTest(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody CreateUserHabitsRequest createUserHabitsRequest) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return userHabitsService.createANewHabitsForUser(userId, createUserHabitsRequest);
    }

    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public List<UserHabitsStorage> getAllHabits() {
        return userHabitsService.getAllUserHabits();
    }


}
