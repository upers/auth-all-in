package com.msavchuk.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class FlywayConfig {
    private static final String FLYWAY_TABLE_NAME = "flyway_schema_history";

    @Autowired
    private DataSource dataSource;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            if (!isFlywayInitialized()) {
                flyway.baseline();
            }
            flyway.migrate();
        };
    }

    @SneakyThrows private boolean isFlywayInitialized() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet result = metadata.getTables(null, null, FLYWAY_TABLE_NAME, null);
            return result.next();
        } catch (SQLException e) {
            throw new SQLException("Failed to check if Flyway is initialized", e);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

}