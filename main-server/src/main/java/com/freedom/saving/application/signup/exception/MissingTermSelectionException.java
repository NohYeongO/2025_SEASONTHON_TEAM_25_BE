package com.freedom.saving.application.signup.exception;

import java.util.List;

public class MissingTermSelectionException extends RuntimeException {
    public MissingTermSelectionException(List<Integer> candidates) {
        super("기간 선택이 필요합니다. 지원 기간: " + String.valueOf(candidates));
    }
}
