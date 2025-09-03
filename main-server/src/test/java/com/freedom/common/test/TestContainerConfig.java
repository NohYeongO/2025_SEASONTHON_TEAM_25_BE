package com.freedom.common.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public abstract class TestContainerConfig {


    protected static MySQLContainer<?> mysql;

    static {
        mysql = new MySQLContainer<>("mysql:8.4.5")
                .withDatabaseName("test_financial_freedom")
                .withUsername("test_user")
                .withPassword("test_password")
                .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci", "--default-time-zone=+09:00")
                .withReuse(true);

        mysql.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Database 설정
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        // JPA 설정 - 테스트용 최적화
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false"); // 로그 간소화
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "false");

        // 기타 테스트 최적화 설정
        registry.add("logging.level.org.springframework.web", () -> "WARN");
        registry.add("logging.level.org.testcontainers", () -> "WARN");
        registry.add("logging.level.com.zaxxer.hikari", () -> "WARN");
    }
}
