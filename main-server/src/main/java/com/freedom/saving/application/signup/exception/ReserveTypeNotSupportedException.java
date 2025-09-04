package com.freedom.saving.application.signup.exception;

public class ReserveTypeNotSupportedException extends RuntimeException {
    public ReserveTypeNotSupportedException(int termMonths, String reserveType) {
        super("지원하지 않는 적립유형입니다. term=" + termMonths + ", reserveType=" + reserveType);
    }
}


