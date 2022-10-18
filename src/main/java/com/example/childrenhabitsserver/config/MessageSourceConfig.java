package com.example.childrenhabitsserver.config;

import com.example.childrenhabitsserver.base.MultipleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceConfig {
    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        MultipleMessageSource messageSource
                = new MultipleMessageSource();
        messageSource.setFileWildCard("message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
