package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.common.request.habits.CreateHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.service.HabitsService;
import com.example.childrenhabitsserver.service.SendEmailNotificationService;
import com.example.childrenhabitsserver.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public List<HabitsStorage> getAllHabits(){
        return habitsService.getAllHabits();
    }


}
