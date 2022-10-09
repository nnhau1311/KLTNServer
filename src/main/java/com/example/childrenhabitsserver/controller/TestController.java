package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
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
}
