package com.freedom.saving.application.signup.exception;

import java.util.List;

public class MissingReserveTypeSelectionException extends RuntimeException {
    public MissingReserveTypeSelectionException(int termMonths, List<String> candidates) {
        super("적립유형 선택이 필요합니다. term=" + termMonths + ", 지원유형=" + String.valueOf(candidates));
    }
}
