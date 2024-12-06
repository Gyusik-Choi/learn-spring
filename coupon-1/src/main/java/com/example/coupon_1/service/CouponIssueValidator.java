package com.example.coupon_1.service;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.model.CouponIssue;
import com.example.coupon_1.repository.mysql.CouponIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.coupon_1.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;

@RequiredArgsConstructor
@Service
public class CouponIssueValidator {

    private final CouponIssueRepository couponIssueRepository;

    public Boolean availableUserIssueQuantity(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
        return issue == null;
    }

    /**
     * Coupon 엔티티로 조회한 쿠폰이 발급 가능한지 검증
     */
    public void checkAvailableCouponIssue(Coupon coupon, long userId) {
        if (!availableUserIssueQuantity(coupon.getId(), userId))
            throw new CouponIssueException(
                    DUPLICATED_COUPON_ISSUE,
                    "이미 발급 요청이 처리 됐습니다. couponId=%s, userId=%s".formatted(coupon.getId(), userId));
    }
}
