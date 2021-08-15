package com.msavchuk.config;

import com.msavchuk.email.RegistrationEmailSender;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class SpringTestConfig {

    @Bean
    @Primary
    public RegistrationEmailSender nameService() {
        return Mockito.mock(RegistrationEmailSender.class);
    }

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
}
