package com.example.childrenhabitsserver.action;

import com.example.childrenhabitsserver.common.constant.RabbitMQQueue;
import com.example.childrenhabitsserver.model.NotificationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueueHandler {

    @RabbitListener(queues = RabbitMQQueue.NOTIFY_QUEUE)
    public void deleteFile(NotificationModel notificationModel) {
        try {
            log.info("test Rabbits: {}", notificationModel.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
