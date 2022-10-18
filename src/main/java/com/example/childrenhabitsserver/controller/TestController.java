package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.service.SendEmailNotificationService;
import com.example.childrenhabitsserver.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    private final TestService testService;
    private final SendEmailNotificationService sendEmailNotificationService;

    public TestController(TestService testService, SendEmailNotificationService sendEmailNotificationService) {
        this.testService = testService;
        this.sendEmailNotificationService = sendEmailNotificationService;
    }

    @RequestMapping(value = "/testSave", method = RequestMethod.POST)
    public String saveTest(){


        testService.testSaveRepo();
        return "done";
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public List<TestJPA> getAllTest(){
//        log.info(SecurityContextHolder.getContext().getAuthentication().getDetails());
        return testService.getAllRepo();
    }

    @RequestMapping(value = "/test-email", method = RequestMethod.POST)
    public String testEmail(@RequestBody NotificationModel notificationModel){
//        log.info(SecurityContextHolder.getContext().getAuthentication().getDetails());
        sendEmailNotificationService.sendEmail(notificationModel);
        return "success";
    }
}
