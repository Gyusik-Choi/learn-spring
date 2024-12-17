package com.example.coupon_1.service;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.model.CouponIssue;
import com.example.coupon_1.repository.mysql.CouponIssueJpaRepository;
import com.example.coupon_1.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.coupon_1.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
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
}
