package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ScheduleService {
    private final RabbitTemplate rabbitTemplate;
    private final UserCustomService userCustomService;
    private final UserHabitsService userHabitsService;
    private final SendEmailNotificationService sendEmailNotificationService;

    public ScheduleService(RabbitTemplate rabbitTemplate, UserCustomService userCustomService, UserHabitsService userHabitsService, SendEmailNotificationService sendEmailNotificationService) {
        this.rabbitTemplate = rabbitTemplate;
        this.userCustomService = userCustomService;
        this.userHabitsService = userHabitsService;
        this.sendEmailNotificationService = sendEmailNotificationService;
    }

    @Scheduled(cron = "0 0 0/8 * * *")
//    @Scheduled(cron = "0/1 * * * * *")
//    @Scheduled(cron = "0 0/1 * * * *")
    public void remindUsingApp() {
        log.info(">>>>>>>>>>>>>>>>> remindUsingApp Schedule <<<<<<<<<<<<<<<<");
        List<UserCustomStorage> listUser = userCustomService.findAll();
        for (UserCustomStorage userStorage: listUser) {
//            userStorage.setAccessDate(DateTimeUtils.addDate(new Date(), 5));
            if (userStorage.getAccessDate() == null || userStorage.getUsername().equals("admin")) {
                continue;
            }
            Random random = new Random();
            Integer numberRandomDate = random.nextInt();
            numberRandomDate = (numberRandomDate % 9) + 1;
            if (numberRandomDate < 0) {
                numberRandomDate *= -1;
            }
            Date currentDate = new Date();
            if (DateTimeUtils.getDifferenceDays(currentDate, userStorage.getAccessDate()) >= numberRandomDate) {
                // Gửi email
                Map<String, Object> scopes = new HashMap<>();
                scopes.put("userFullName", userStorage.getUserFullName());
                scopes.put("userName", userStorage.getUsername());
                scopes.put("numberRandomDate", numberRandomDate);
                NotificationModel notificationModel = NotificationModel.builder()
                        .to(userStorage.getEmail())
                        .template("NotifyReminUserLongTimeDontAccess")
                        .scopes(scopes)
                        .subject("Hãy quay lại giúp trẻ thực hiện thói quen nào!")
                        .build();
                sendEmailNotificationService.sendEmail(notificationModel);
            }
        }

    }


    @Scheduled(cron = "0 0 23 * * *")
//    @Scheduled(cron = "0/1 * * * * *")
//    @Scheduled(cron = "0 0/1 * * * *")
    public void remindMissingDateAttendance() {
        log.info(">>>>>>>>>>>>>>>>> remindUsingApp Schedule <<<<<<<<<<<<<<<<");
        List<UserHabitsStorage> userHabitsStorageList = userHabitsService.getAllUserHabits();
        for (UserHabitsStorage userHabitsStorage: userHabitsStorageList) {

        }
    }

}
