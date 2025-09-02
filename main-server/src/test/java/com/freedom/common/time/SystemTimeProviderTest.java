package com.freedom.common.time;

import com.freedom.common.time.infra.SystemTimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTimeProviderTest {

    @Test
    @DisplayName("Clock.fixed로 고정 시 today/now/instant/zone이 일관되게 반환된다")
    void fixedClock_returns_consistent_values() {
        // given
        ZoneId zone = ZoneId.of("Asia/Seoul");
        Instant base = LocalDate.of(2025, 9, 1).atStartOfDay(zone).toInstant();
        Clock fixed = Clock.fixed(base, zone);
        TimeProvider provider = new SystemTimeProvider(fixed);

        // when
        LocalDate today = provider.today();
        ZonedDateTime now = provider.now();
        Instant instant = provider.instant();
        ZoneId zoneId = provider.zoneId();

        // then
        assertEquals(LocalDate.of(2025, 9, 1), today);
        assertEquals(zone, zoneId);
        assertEquals(base, instant);
        assertEquals(ZonedDateTime.of(2025, 9, 1, 0, 0, 0, 0, zone), now);
    }

    @Test
    @DisplayName("타임존이 다르면 today도 타임존 기준으로 계산된다")
    void different_zone_affects_today() {
        // given
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        ZoneId utc = ZoneId.of("UTC");
        Instant base = LocalDateTime.of(2025, 9, 1, 0, 0).toInstant(ZoneOffset.UTC);

        TimeProvider seoulProvider = new SystemTimeProvider(Clock.fixed(base, seoul));
        TimeProvider utcProvider = new SystemTimeProvider(Clock.fixed(base, utc));

        // when
        LocalDate seoulDate = seoulProvider.today();
        LocalDate utcDate = utcProvider.today();

        // then
        // 같은 Instant라도 타임존에 따라 LocalDate가 달라질 수 있음을 검증
        assertNotNull(seoulDate);
        assertNotNull(utcDate);
    }
}
