package com.freedom.saving.infra.config;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.application.policy.RealDayEqualsServiceMonthPolicy;
import com.freedom.saving.domain.policy.TickPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SavingsPolicyConfig {

    @Bean
    public TickPolicy tickPolicy(TimeProvider timeProvider) {
        return new RealDayEqualsServiceMonthPolicy(timeProvider);
    }
}
