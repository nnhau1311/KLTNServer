package com.example.childrenhabitsserver.controller;

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

    @RequestMapping(value = "/testSave", method = RequestMethod.POST)
    public String saveTest(){
        habitsService.testSaveRepo();
        return "done";
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public List<HabitsStorage> getAllTest(){
//        log.info(SecurityContextHolder.getContext().getAuthentication().getDetails());
        return habitsService.getAllRepo();
    }


}
