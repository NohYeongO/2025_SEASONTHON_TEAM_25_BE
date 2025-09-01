package com.freedom.saving;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.application.SavingsDateService;
import com.freedom.saving.application.policy.RealDayEqualsServiceMonthPolicy;
import com.freedom.saving.domain.policy.TickPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsDateServiceTest {

    private static class FixedTimeProvider implements TimeProvider {
        private final Clock clock;
        FixedTimeProvider(Clock clock) { this.clock = clock; }
        @Override public Instant instant() { return clock.instant(); }
        @Override public ZonedDateTime now() { return ZonedDateTime.now(clock); }
        @Override public LocalDate today() { return LocalDate.now(clock); }
        @Override public ZoneId zoneId() { return clock.getZone(); }
    }

    @Test
    @DisplayName("SavingsDateService는 TickPolicy와 TimeProvider를 조합해 날짜/진행을 계산한다")
    void compute_dates_and_progress() {
        // given
        ZoneId zone = ZoneId.of("Asia/Seoul");
        Clock fixed = Clock.fixed(LocalDate.of(2025, 9, 5).atStartOfDay(zone).toInstant(), zone);
        TimeProvider tp = new FixedTimeProvider(fixed);
        TickPolicy policy = new RealDayEqualsServiceMonthPolicy(tp);
        SavingsDateService svc = new SavingsDateService(tp, policy);

        // when
        LocalDate join = LocalDate.of(2025, 9, 1);
        int termMonths = 12;

        LocalDate first = svc.firstTransferDate(join);
        LocalDate maturity = svc.maturityDate(join, termMonths);
        LocalDate next = svc.nextTransferDate(join, 3); // 현재까지 3회차 처리 완료 → 다음은 9/5
        int currentTick = svc.currentTick(join, termMonths); // today=9/5 → 3

        // then
        assertEquals(LocalDate.of(2025, 9, 2), first);
        assertEquals(LocalDate.of(2025, 9, 13), maturity);
        assertEquals(LocalDate.of(2025, 9, 5), next);
        assertEquals(3, currentTick);
    }
}
