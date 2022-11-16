package com.example.childrenhabitsserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ScheduleService {

    @Scheduled(cron = "0 0 8 * * *")
//    @Scheduled(cron = "0 0/1 * * * *")
    public void remindUsingApp() {
        log.info(">>>>>>>>>>>>>>>>> remindUsingApp Schedule <<<<<<<<<<<<<<<<");
    }
}
