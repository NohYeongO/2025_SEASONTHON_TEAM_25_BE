package com.freedom.saving.application.signup.exception;

public class ProductSnapshotNotFoundException extends RuntimeException {
    public ProductSnapshotNotFoundException(Long id) {
        super("상품 스냅샷이 존재하지 않습니다. id=" + id);
    }
}
