package com.freedom.admin.api;

import com.freedom.admin.api.dto.AdminAuthCheckResponse;
import com.freedom.admin.api.dto.AdminLoginRequest;
import com.freedom.admin.api.dto.AdminLoginResponse;
import com.freedom.admin.api.dto.AdminLogoutResponse;
import com.freedom.auth.application.AuthFacade;
import com.freedom.auth.application.dto.TokenDto;
import com.freedom.auth.domain.UserRole;
import com.freedom.common.exception.custom.InvalidPasswordException;
import com.freedom.common.exception.custom.UserNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 인증 API 컨트롤러
 */
@RestController
@RequestMapping("/admin/api/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthFacade authFacade;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request, 
                                                   HttpServletResponse response) {
        try {
            TokenDto tokenDto = authFacade.login(request.getEmail(), request.getPassword());
            
            if (!UserRole.ADMIN.name().equals(tokenDto.getUser().getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AdminLoginResponse.failure("관리자 권한이 필요합니다."));
            }

            setAuthCookies(response, tokenDto.getAccessToken(), tokenDto.getRefreshToken());

            return ResponseEntity.ok(AdminLoginResponse.success(
                tokenDto.getAccessToken(),
                tokenDto.getUser().getEmail(),
                "/admin/dashboard"
            ));

        } catch (UserNotFoundException | InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AdminLoginResponse.failure("이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AdminLogoutResponse> logout(HttpServletResponse response) {
        clearAuthCookies(response);
        return ResponseEntity.ok(AdminLogoutResponse.success("/admin/login"));
    }

    @GetMapping("/check")
    public ResponseEntity<AdminAuthCheckResponse> check() {
        return ResponseEntity.ok(AdminAuthCheckResponse.authenticated());
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = createCookie("admin_access_token", accessToken, 30 * 60);
        Cookie refreshCookie = createCookie("admin_refresh_token", refreshToken, 14 * 24 * 60 * 60);
        
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = createCookie("admin_access_token", "", 0);
        Cookie refreshCookie = createCookie("admin_refresh_token", "", 0);
        
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/admin");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
