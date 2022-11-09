package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.auth.JwtTokenProvider;
import com.example.childrenhabitsserver.base.request.BasePageRequest;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.userhabits.AttendanceUserHabitsContentRequest;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.service.UserHabitsService;
import io.swagger.annotations.ApiOperation;
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
    public WrapResponse<UserHabitsStorage> createNewHabitsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody CreateUserHabitsRequest createUserHabitsRequest) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(userHabitsService.createANewHabitsForUser(userId, createUserHabitsRequest));
    }

    @RequestMapping(value = "/attendance-habits-content", method = RequestMethod.POST)
    public WrapResponse<String> attendancePerHabitsContent(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody AttendanceUserHabitsContentRequest request) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(userHabitsService.attendancePerHabitsContent(userId, request));
    }

    // QUERY ==============================================================
    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public List<UserHabitsStorage> getAllHabits() {
        return userHabitsService.getAllUserHabits();
    }

    /**
     *
     * @param userHabitsId id của thói quen của người dùng (Không phải là habitsId - id của thói quen)
     * @return
     */
    @ApiOperation(value = "Lấy thói quen của người dùng bằng id", notes = "userHabitsId id của thói quen của người dùng (Không phải là habitsId - id của thói quen")
    @RequestMapping(value = "/get-habits/{userHabitsId}", method = RequestMethod.GET)
    public WrapResponse<UserHabitsStorage> getUserHabitsById(@PathVariable String userHabitsId) {
        return WrapResponse.ok(userHabitsService.getUserHabitsById(userHabitsId));
    }

    @RequestMapping(value = "/get-habits", method = RequestMethod.GET)
    public WrapResponse<Page<UserHabitsStorage>> getUserHabits(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody BasePageRequest basePageRequest) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(userHabitsService.getListUserHabits(userId, basePageRequest));
    }

}
