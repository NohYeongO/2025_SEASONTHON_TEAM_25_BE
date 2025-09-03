package com.freedom.saving.application.signup;


/**
 * 가입 결과(애플리케이션 계층)
 * - 최소한 생성된 구독 ID만 반환. 필요에 따라 확장 가능
 */
public class OpenSubscriptionResult {

    private final Long subscriptionId;

    public OpenSubscriptionResult(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }
}
