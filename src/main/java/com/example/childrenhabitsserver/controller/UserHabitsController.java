package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.auth.JwtTokenProvider;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.service.UserHabitsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public UserHabitsStorage createNewHabitsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody CreateUserHabitsRequest createUserHabitsRequest) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return userHabitsService.createANewHabitsForUser(userId, createUserHabitsRequest);
    }

    // QUERY ==============================================================
    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public List<UserHabitsStorage> getAllHabits() {
        return userHabitsService.getAllUserHabits();
    }

    @RequestMapping(value = "/get-habits", method = RequestMethod.GET)
    public WrapResponse<Page<UserHabitsStorage>> getUserHabits(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(userHabitsService.getListUserHabits(userId));
    }

}
