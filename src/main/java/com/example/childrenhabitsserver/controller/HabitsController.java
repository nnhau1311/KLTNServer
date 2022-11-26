package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.base.request.BasePageRequest;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.constant.HabitsStatus;
import com.example.childrenhabitsserver.common.request.habits.CreateHabitsRequest;
import com.example.childrenhabitsserver.common.request.habits.UpdateHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.service.HabitsService;
import com.example.childrenhabitsserver.service.SendEmailNotificationService;
import com.example.childrenhabitsserver.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("habits")
@Slf4j
public class HabitsController {

    private final HabitsService habitsService;

    public HabitsController(HabitsService habitsService) {
        this.habitsService = habitsService;
    }

    @RequestMapping(value = "/create-a-new", method = RequestMethod.POST)
    public WrapResponse<HabitsStorage> saveNewHabits(@RequestBody CreateHabitsRequest createHabitsRequest){
        return WrapResponse.ok(habitsService.createANewHabits(createHabitsRequest));
    }

    @RequestMapping(value = "/update/{habitsId}", method = RequestMethod.POST)
    public WrapResponse<HabitsStorage> updateHabits(@PathVariable String habitsId, @RequestBody UpdateHabitsRequest updateHabitsRequest){
        return WrapResponse.ok(habitsService.updateAHabits(habitsId, updateHabitsRequest));
    }

    @RequestMapping(value = "/disable/{habitsId}", method = RequestMethod.POST)
    public WrapResponse<HabitsStorage> disableHabits(@PathVariable String habitsId){
        return WrapResponse.ok(habitsService.updateStatusHabits(habitsId, HabitsStatus.DISABLE));
    }

    @RequestMapping(value = "/active/{habitsId}", method = RequestMethod.POST)
    public WrapResponse<HabitsStorage> activeHabits(@PathVariable String habitsId){
        return WrapResponse.ok(habitsService.updateStatusHabits(habitsId, HabitsStatus.NEW));
    }

    // QUERY ========================================
    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public WrapResponse<Page<HabitsStorage>> getAllHabitsActive(@RequestBody BasePageRequest basePageRequest){
        return WrapResponse.ok(habitsService.getAllHabits(basePageRequest));
    }

    @RequestMapping(value = "/get-all-admin", method = RequestMethod.POST)
    public WrapResponse<Page<HabitsStorage>> getAllHabitsAdmin(@RequestBody BasePageRequest basePageRequest){
        return WrapResponse.ok(habitsService.getAllHabitsAdmin(basePageRequest));
    }

    @RequestMapping(value = "/find-by-id/{habitsId}", method = RequestMethod.POST)
    public WrapResponse<HabitsStorage> findHabitsById(@PathVariable String habitsId){
        return WrapResponse.ok(habitsService.findById(habitsId));
    }
}
