package com.freedom.common.exception.custom;

import lombok.Getter;

@Getter
public class UserQuizNotFoundException extends RuntimeException {
    public UserQuizNotFoundException(String message) {
        super(message);
    }
}
