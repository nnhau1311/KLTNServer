package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.auth.JwtTokenProvider;
import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.base.request.BasePageRequest;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.userhabits.AttendanceUserHabitsContentRequest;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.common.request.userhabits.StatisticUserHabitsRequest;
import com.example.childrenhabitsserver.common.request.userhabits.UpdateUserHabitsFullDataRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.service.UserHabitsService;
import com.example.childrenhabitsserver.utils.DateTimeUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public WrapResponse<UserHabitsStorage> attendancePerHabitsContent(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody AttendanceUserHabitsContentRequest request) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(userHabitsService.attendancePerHabitsContent(userId, request));
    }

    // UPDATE =============================================================

    @ApiOperation(value = "C???p nh???t th??i quen c???a ng?????i d??ng b???ng id", notes = "G???i l??n to??n b??? th??ng tin, s??? b??? null data n???u thi???u b???t k?? field n??o")
    @RequestMapping(value = "/update-habits", method = RequestMethod.GET)
    public WrapResponse<Object> deleteUserHabitsById(@RequestBody UpdateUserHabitsFullDataRequest request) {
        return WrapResponse.ok(userHabitsService.updateUserHabits(request));
    }

    // DELETE =============================================================

    @ApiOperation(value = "Xo?? th??i quen c???a ng?????i d??ng b???ng id", notes = "userHabitsId id c???a th??i quen c???a ng?????i d??ng (Kh??ng ph???i l?? habitsId - id c???a th??i quen")
    @RequestMapping(value = "/delete-habits/{userHabitsId}", method = RequestMethod.POST)
    public WrapResponse<Object> deleteUserHabitsById(@PathVariable String userHabitsId) {
        return WrapResponse.ok(userHabitsService.deleteUserHabitsId(userHabitsId));
    }

    // QUERY ==============================================================
    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public List<UserHabitsStorage> getAllHabits() {
        return userHabitsService.getAllUserHabits();
    }

    /**
     *
     * @param userHabitsId id c???a th??i quen c???a ng?????i d??ng (Kh??ng ph???i l?? habitsId - id c???a th??i quen)
     * @return
     */
    @ApiOperation(value = "L???y th??i quen c???a ng?????i d??ng b???ng id", notes = "userHabitsId id c???a th??i quen c???a ng?????i d??ng (Kh??ng ph???i l?? habitsId - id c???a th??i quen")
    @RequestMapping(value = "/get-habits/{userHabitsId}", method = RequestMethod.GET)
    public WrapResponse<UserHabitsStorage> getUserHabitsById(@PathVariable String userHabitsId) {
        return WrapResponse.ok(userHabitsService.getUserHabitsById(userHabitsId));
    }

    @RequestMapping(value = "/get-habits", method = RequestMethod.POST)
    public WrapResponse<Page<UserHabitsStorage>> getUserHabits(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody BasePageRequest basePageRequest) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        return WrapResponse.ok(userHabitsService.getListUserHabits(userId, basePageRequest));
    }

    @ApiOperation(value = "Th???ng k?? th??i quen c???a ng?????i d??ng b???ng id", notes = "Truy???n timeStatistic l?? MONTH ho???c WEEK")
    @RequestMapping(value = "/statistic-habits/{timeStatistic}", method = RequestMethod.GET)
    public WrapResponse<Object> statisticUserHabits(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader, @RequestBody StatisticUserHabitsRequest request) {
        String token = tokenHeader.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        if (request.getStartDateFilter() == null || request.getEndDateFilter() == null) {
            throw new ServiceException("Y??u c???u th???ng k?? kh??ng h???p l???");
        }
        log.info("dateStartFilter {}", request.getStartDateFilter());
        log.info("endStartFilter {}", request.getEndDateFilter());
        return WrapResponse.ok(userHabitsService.statisticUserHabits(userId, request.getStartDateFilter(), request.getEndDateFilter()));
    }
}
