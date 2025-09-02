package com.freedom.saving.application.job;

import com.freedom.saving.application.policy.ProductSnapshotSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "jobs.fss-sync.enabled", havingValue = "true", matchIfMissing = true)
public class FssSnapshotSyncJob {

    private final ProductSnapshotSyncService syncService;

    /**
     * 매일 03:00(Asia/Seoul) 실행.설정
     * zone을 명시해 서버 TZ와 무관하게 일정 고정
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void runDaily() {
        log.info("[FSS SYNC][JOB] start");
        ProductSnapshotSyncService.SyncResult r = syncService.syncAll();
        log.info("[FSS SYNC][JOB] end pages={}, products={}, options={}, skipped={}",
                r.getPages(), r.getProducts(), r.getOptions(), r.getSkipped());
    }

    /**
     * 수동 실행용 진입점(내부 관리 API나 테스트에서 호출)
     * @Scheduled 없이 동일 동작 보장
     */
    public void runOnce() {
        runDaily();
    }
}
