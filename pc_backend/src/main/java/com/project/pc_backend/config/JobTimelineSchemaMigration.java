package com.project.pc_backend.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobTimelineSchemaMigration {

        private final JdbcTemplate jdbcTemplate;

    public JobTimelineSchemaMigration(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        @EventListener(ApplicationReadyEvent.class)
        public void migrate() {
                Integer reservedColumnCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'job_timeline'
                  AND COLUMN_NAME = 'desc'
                """,
                Integer.class
        );

        Integer safeColumnCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'job_timeline'
                  AND COLUMN_NAME = 'job_description'
                """,
                Integer.class
        );

        if (reservedColumnCount != null && reservedColumnCount > 0
                && safeColumnCount != null && safeColumnCount == 0) {
            jdbcTemplate.execute(
                    "ALTER TABLE job_timeline CHANGE COLUMN `desc` job_description TEXT"
            );
            return;
        }

        if (reservedColumnCount != null && reservedColumnCount > 0
                && safeColumnCount != null && safeColumnCount > 0) {
            jdbcTemplate.execute(
                    "UPDATE job_timeline SET job_description = COALESCE(NULLIF(job_description, ''), `desc`)"
            );
        }
    }
}