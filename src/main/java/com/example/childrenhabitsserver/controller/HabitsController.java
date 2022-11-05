package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.base.response.WrapResponse;
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
    public HabitsStorage saveTest(@RequestBody CreateHabitsRequest createHabitsRequest){
        return habitsService.createANewHabits(createHabitsRequest);
    }

    @RequestMapping(value = "/update/{habitsId}", method = RequestMethod.POST)
    public HabitsStorage saveTest(@PathVariable String habitsId, @RequestBody UpdateHabitsRequest updateHabitsRequest){
        return habitsService.updateAHabits(habitsId, updateHabitsRequest);
    }

    // QUERY ========================================
    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public WrapResponse<Page<HabitsStorage>> getAllHabitsActive(){
        return WrapResponse.ok(habitsService.getAllHabits());
    }


}
