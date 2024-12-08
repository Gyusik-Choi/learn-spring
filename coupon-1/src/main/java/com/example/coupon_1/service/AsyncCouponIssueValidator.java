package com.example.coupon_1.service;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.repository.redis.RedisRepository;
import com.example.coupon_1.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.coupon_1.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;
import static com.example.coupon_1.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static com.example.coupon_1.util.CouponRedisUtil.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueValidator {

    private final RedisRepository redisRepository;

    public Boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) return true;
        return totalQuantity > redisRepository.sCard(getIssueRequestKey(couponId));
    }

    public Boolean availableUserIssueQuantity(long couponId, long userId) {
        return !redisRepository.sIsMember(getIssueRequestKey(couponId), String.valueOf(userId));
    }

    /**
     * 파라미터 타입만 다르고 구현이 중복되고 있다
     * 중복 개선이 필요하다
     */
    public void checkAvailableCouponIssue(Coupon coupon, long userId) {
        if (!availableTotalIssueQuantity(coupon.getTotalQuantity(), coupon.getId()))
            throw new CouponIssueException(
                    INVALID_COUPON_ISSUE_QUANTITY,
                    "발급 가능한 수량을 초과 합니다. couponId=%s, userId=%s".formatted(coupon.getId(), userId));

        if (!availableUserIssueQuantity(coupon.getId(), userId))
            throw new CouponIssueException(
                    DUPLICATED_COUPON_ISSUE,
                    "이미 발급 요청이 처리 됐습니다. couponId=%s, userId=%s".formatted(coupon.getId(), userId));
    }

    /**
     * CouponRedisEntity 로 조회한 쿠폰이 발급 가능한지 검증
     */
    public void checkAvailableCouponIssue(CouponRedisEntity coupon, long userId) {
        if (!availableTotalIssueQuantity(coupon.totalQuantity(), coupon.id()))
            throw new CouponIssueException(
                    INVALID_COUPON_ISSUE_QUANTITY,
                    "발급 가능한 수량을 초과 합니다. couponId=%s, userId=%s".formatted(coupon.id(), userId));

        if (!availableUserIssueQuantity(coupon.id(), userId))
            throw new CouponIssueException(
                    DUPLICATED_COUPON_ISSUE,
                    "이미 발급 요청이 처리 됐습니다. couponId=%s, userId=%s".formatted(coupon.id(), userId));
    }
}
