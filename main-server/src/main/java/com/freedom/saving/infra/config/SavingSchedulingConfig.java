package com.freedom.saving.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄러 전역 활성화.
 * 기능(적금) 하위에서 활성화하여 범위를 명확히 함
 */
@Configuration
@EnableScheduling
public class SavingSchedulingConfig {
}
