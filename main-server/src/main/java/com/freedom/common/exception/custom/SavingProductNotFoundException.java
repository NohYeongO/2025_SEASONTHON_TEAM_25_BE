package com.freedom.common.exception.custom;

public class SavingProductNotFoundException extends RuntimeException {
  public SavingProductNotFoundException(Long id) {
    super("적금 상품 스냅샷을 찾을 수 없습니다. id = " + id);
  }
}
