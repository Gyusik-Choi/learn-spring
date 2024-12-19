package com.example.coupon_1.service;

import com.example.coupon_1.event.CouponIssueCompleteEvent;
import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.model.CouponIssue;
import com.example.coupon_1.repository.mysql.CouponIssueJpaRepository;
import com.example.coupon_1.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.coupon_1.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
        publishCouponEvent(coupon);
    }

    @Transactional
    public void issueWithLock(long couponId, long userId) {
        synchronized (this) {
            Coupon coupon = findCoupon(couponId);
            coupon.issue();
            saveCouponIssue(couponId, userId);
        }
    }

    @Transactional
    public void issueWithXLock(long couponId, long userId) {
        Coupon coupon = findCouponWithXLock(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        Coupon coupon = couponJpaRepository
                .findById(couponId)
                .orElseThrow(() ->
                    new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
        coupon.checkIssuableCoupon();
        return coupon;
    }

    @Transactional
    public Coupon findCouponWithXLock(long couponId) {
        Coupon coupon = couponJpaRepository
                .findCouponWithXLock(couponId)
                .orElseThrow(() ->
                    new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
        coupon.checkIssuableCoupon();
        return coupon;
    }

    /**
     * <a href="https://stackoverflow.com/questions/45630211/spring-transaction-when-calling-private-method">참고링크</a>
     * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html">TransactionSynchronizationManager</a>
     * <a href="https://hudi.blog/spring-transaction-synchronization-and-abstraction/">블로그</a>
     * private method 에는 '@Transactional' 애노테이션을 사용할 수 없다
     * 스프링의 aop 는 프록시 기반인데 프록시는 private method 에는 적용할 수 없다
     * 그렇다고 트랜잭션을 private method 에 적용할 수 없는건 아니다
     * '@Transactional' 애노테이션이 있는 public method 에서
     * '@Transactional' 애노테이션이 없는 private method 를 호출하면
     * 이미 트랜잭션이 시작했기 때문에 트랜잭션이 그대로 적용된다.
     */
    private void saveCouponIssue(long couponId, long userId) {
        CouponIssue issue = CouponIssue
                .builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(issue);
    }

    /**
     * 쿠폰 발행이 완료된 경우
     * (기간이 종료 되었거나 발급 수량이 소진된 경우)
     * 이벤트를 발행한다
     * 이벤트를 발행해서 쿠폰에 대한 Redis 캐시와 로컬 캐시를 갱신한다
     * 쿠폰의 수량 정보가 갱신되고 쿠폰의 발행이 완료된 정보를
     * CouponRedisEntity 에도 추가해서 쿠폰을 발행하는 로직까지 오지 않고
     * AsyncCouponIssueService 의 issue 로직에서
     * CouponRedisEntity 를 통한 유효성 검사를 통해 걸러낸다
     */
    private void publishCouponEvent(Coupon coupon) {
        if (coupon.isIssueComplete()) eventPublisher.publishEvent(new CouponIssueCompleteEvent(coupon.getId()));
    }
}
