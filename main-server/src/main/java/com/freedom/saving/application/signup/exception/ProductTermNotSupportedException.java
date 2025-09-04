package com.freedom.saving.application.signup.exception;

public class ProductTermNotSupportedException extends RuntimeException {
    public ProductTermNotSupportedException(int termMonths) {
        super("지원하지 않는 기간입니다. termMonths=" + termMonths);
    }
}
