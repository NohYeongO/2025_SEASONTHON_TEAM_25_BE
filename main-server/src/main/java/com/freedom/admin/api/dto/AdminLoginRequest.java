package com.freedom.admin.api.dto;

import lombok.Getter;

@Getter
public class AdminLoginRequest {
    private String email;
    private String password;
}
