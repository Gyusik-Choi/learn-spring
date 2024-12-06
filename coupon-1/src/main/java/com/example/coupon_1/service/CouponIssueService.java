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
    private final CouponIssueValidator couponIssueValidator;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        couponIssueValidator.checkAvailableCouponIssue(coupon, userId);
        saveCouponIssue(couponId, userId);
    }

    @Transactional
    public void issueWithLock(long couponId, long userId) {
        synchronized (this) {
            Coupon coupon = findCoupon(couponId);
            coupon.issue();
            couponIssueValidator.checkAvailableCouponIssue(coupon, userId);
            saveCouponIssue(couponId, userId);
        }
    }

    @Transactional
    public void issueWithXLock(long couponId, long userId) {
        Coupon coupon = findCouponWithXLock(couponId);
        coupon.issue();
        couponIssueValidator.checkAvailableCouponIssue(coupon, userId);
        saveCouponIssue(couponId, userId);
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() ->
                new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
    }

    @Transactional
    public Coupon findCouponWithXLock(long couponId) {
        return couponJpaRepository.findCouponWithXLock(couponId).orElseThrow(() ->
                new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));
    }

    private void saveCouponIssue(long couponId, long userId) {
        CouponIssue issue = CouponIssue
                .builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(issue);
    }
}
