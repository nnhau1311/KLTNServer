package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.user.CreateNewUserRequest;
import com.example.childrenhabitsserver.service.StatisticService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/statistic")
@Slf4j
@ApiOperation(value = "Thống kê", notes = "Toàn bộ thống kê được viết ở controller này")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @RequestMapping(value = "/user/basic", method = RequestMethod.POST)
    public WrapResponse<Object> statisticUserBasic() {
        return WrapResponse.ok(statisticService.statisticUserBasic());
    }

    @RequestMapping(value = "/habits/basic", method = RequestMethod.POST)
    public WrapResponse<Object> statisticHabitsBasic() {
        return WrapResponse.ok(statisticService.statisticHabitsBasic());
    }
}
