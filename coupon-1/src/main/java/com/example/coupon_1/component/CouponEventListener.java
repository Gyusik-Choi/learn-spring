package com.example.coupon_1.component;

import com.example.coupon_1.event.CouponIssueCompleteEvent;
import com.example.coupon_1.service.CouponCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class CouponEventListener {

    private final CouponCacheService couponCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void issueComplete(CouponIssueCompleteEvent event) {
        log.info("issue complete. cache refresh start couponId: %s".formatted(event.couponId()));
        couponCacheService.putCouponCache(event.couponId());
        // 서버를 멀티 인스턴스로 운영한다면
        // 로컬 캐시의 경우 해당 서버 인스턴스만 캐시가 적용되고
        // 다른 서버 인스턴스에는 캐시가 적용되지 않을 수 있다
        couponCacheService.putCouponLocalCache(event.couponId());
        log.info("issue complete. cache refresh end couponId: %s".formatted(event.couponId()));
    }
}
