package com.freedom.home.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.home.api.dto.HomeResponse;
import com.freedom.home.application.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public HomeResponse getHome(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return homeService.getHome(principal.getId());
    }
}
