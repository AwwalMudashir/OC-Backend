package com.project.pc_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatasourceStartupValidation {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String datasourceUsername;

    @PostConstruct
    public void validate() {
        if (datasourceUrl == null || datasourceUrl.isBlank()) {
            throw new IllegalStateException(
                    "Missing database configuration: set SPRING_DATASOURCE_URL or JDBC_DATABASE_URL."
            );
        }

        if (datasourceUrl.contains("HOST") || datasourceUrl.contains("<") || datasourceUrl.contains(">")) {
            throw new IllegalStateException(
                    "Invalid database URL: replace placeholder values in SPRING_DATASOURCE_URL with your real Render database host and database name."
            );
        }

        if (datasourceUsername == null || datasourceUsername.isBlank()) {
            throw new IllegalStateException(
                    "Missing database username: set SPRING_DATASOURCE_USERNAME or JDBC_DATABASE_USERNAME."
            );
        }
    }
}