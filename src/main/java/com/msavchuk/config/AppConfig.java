package com.msavchuk.config;

import com.msavchuk.config.constant.EmailProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Configuration
public class AppConfig {
    private final EmailProperty emailProperty;

    @Autowired
    public AppConfig(EmailProperty emailProperty) {
        this.emailProperty = emailProperty;
    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(Integer.MAX_VALUE);

        return messageSource;
    }

//    @Bean
//    public GracefulShutdown gracefulShutdown() {
//        return new GracefulShutdown();
//    }
//
//    @Bean
//    public ConfigurableServletWebServerFactory webServerFactory(GracefulShutdown gracefulShutdown) {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//        factory.addConnectorCustomizers(gracefulShutdown);
//        return factory;
//    }
}
