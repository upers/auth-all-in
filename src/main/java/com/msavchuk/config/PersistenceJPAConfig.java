package com.msavchuk.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.msavchuk.persistence"})
@EnableJpaRepositories(basePackages = "com.msavchuk.persistence.dao")
public class PersistenceJPAConfig {

}
