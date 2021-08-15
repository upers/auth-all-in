package com.msavchuk.config.constant;


import com.msavchuk.tool.MessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Profile("test-messages")
@Configuration
public class MessageConfig {

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(Integer.MAX_VALUE);

        return messageSource;
    }


    @Bean
    @Autowired
    public MessageFactory messageFactory(@Qualifier("messageSource") MessageSource messageSource) {
        return new MessageFactory(messageSource);
    }

}
