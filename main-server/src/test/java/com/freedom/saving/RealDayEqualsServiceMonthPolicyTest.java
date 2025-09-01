package com.freedom.saving;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.application.policy.RealDayEqualsServiceMonthPolicy;
import com.freedom.saving.domain.policy.TickPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class RealDayEqualsServiceMonthPolicyTest {

    private static class FixedTimeProvider implements TimeProvider {
        private final Clock clock;
        FixedTimeProvider(Clock clock) { this.clock = clock; }
        @Override public Instant instant() { return clock.instant(); }
        @Override public ZonedDateTime now() { return ZonedDateTime.now(clock); }
        @Override public LocalDate today() { return LocalDate.now(clock); }
        @Override public ZoneId zoneId() { return clock.getZone(); }
    }

    @Test
    @DisplayName("가입 다음 날이 첫 납입 '시도일'이며 만기=가입+termMonths(일)")
    void first_and_maturity_dates() {
        // given
        ZoneId zone = ZoneId.of("Asia/Seoul");
        Clock fixed = Clock.fixed(LocalDate.of(2025, 9, 1).atStartOfDay(zone).toInstant(), zone);
        TimeProvider tp = new FixedTimeProvider(fixed);
        TickPolicy policy = new RealDayEqualsServiceMonthPolicy(tp);
        int termMonths = 12;

        // when
        LocalDate join = LocalDate.of(2025, 9, 1);

        // then
        assertEquals(LocalDate.of(2025, 9, 2), policy.calcFirstTransferDate(join));
        assertEquals(LocalDate.of(2025, 9, 13), policy.calcMaturityDate(join, termMonths));
        assertEquals(12, policy.toTotalTicks(termMonths));
    }

    @Test
    @DisplayName("다음 납입일 = 가입+1일+currentTick")
    void nextTransferDate_formula() {
        // given
        ZoneId zone = ZoneId.of("Asia/Seoul");
        Clock fixed = Clock.fixed(LocalDate.of(2025, 9, 1).atStartOfDay(zone).toInstant(), zone);
        TickPolicy policy = new RealDayEqualsServiceMonthPolicy(new FixedTimeProvider(fixed));
        LocalDate join = LocalDate.of(2025, 9, 1);

        // when & then
        assertEquals(LocalDate.of(2025, 9, 2), policy.calcNextTransferDate(join, 0));
        assertEquals(LocalDate.of(2025, 9, 3), policy.calcNextTransferDate(join, 1));
        assertEquals(LocalDate.of(2025, 9, 4), policy.calcNextTransferDate(join, 2));
    }

    @Test
    @DisplayName("현재 tick 추정: 첫 납입 전=0, 만기일 이상=totalTicks, 그 사이=일수")
    void estimateCurrentTick_rules() {
        // given
        ZoneId zone = ZoneId.of("Asia/Seoul");
        Clock fixed = Clock.fixed(LocalDate.of(2025, 9, 5).atStartOfDay(zone).toInstant(), zone);
        TickPolicy policy = new RealDayEqualsServiceMonthPolicy(new FixedTimeProvider(fixed));
        LocalDate join = LocalDate.of(2025, 9, 1);
        int termMonths = 12;

        // when
        int tickMid = policy.estimateCurrentTick(join, LocalDate.of(2025, 9, 5), termMonths); // first=9/2 → 9/2,3,4 경과 → 3
        int tickBefore = policy.estimateCurrentTick(join, LocalDate.of(2025, 9, 1), termMonths); // first 전
        int tickAtMaturity = policy.estimateCurrentTick(join, LocalDate.of(2025, 9, 13), termMonths); // 만기일
        int tickAfterMaturity = policy.estimateCurrentTick(join, LocalDate.of(2025, 9, 14), termMonths); // 만기 이후

        // then
        assertEquals(3, tickMid);
        assertEquals(0, tickBefore);
        assertEquals(12, tickAtMaturity);
        assertEquals(12, tickAfterMaturity);
    }

    @Test
    @DisplayName("예외: termMonths<=0 또는 currentTick<0 또는 joinDate=null")
    void invalid_inputs_throw() {
        // given
        ZoneId zone = ZoneId.of("Asia/Seoul");
        TickPolicy policy = new RealDayEqualsServiceMonthPolicy(
                new FixedTimeProvider(Clock.fixed(Instant.now(), zone)));

        // then
        assertThrows(IllegalArgumentException.class, () -> policy.toTotalTicks(0));
        assertThrows(IllegalArgumentException.class, () -> policy.calcNextTransferDate(LocalDate.now(), -1));
        assertThrows(IllegalArgumentException.class, () -> policy.calcMaturityDate(null, 12));
    }
}
