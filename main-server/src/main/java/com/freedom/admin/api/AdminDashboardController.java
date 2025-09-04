package com.freedom.admin.api;

import com.freedom.admin.api.dto.DashboardStatsResponse;
import com.freedom.admin.application.AdminDashboardService;
import com.freedom.common.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @Loggable("대시보드 통계 조회")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse response = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(response);
    }
}
