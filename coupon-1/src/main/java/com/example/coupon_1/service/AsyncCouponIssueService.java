package com.example.coupon_1.service;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.exception.ErrorCode;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.coupon_1.util.CouponRedisUtil.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;

    /**
     * 유저의 요청을 sorted set 에 적재
     */
    public void issueV1(long couponId, long userId) {
        String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());
    }

    public void issueV2(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        if (!availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId))
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                    "발급 가능한 수량을 초과 합니다. couponId=%s, userId=%s".formatted(couponId, userId));

        if (!availableUserIssueQuantity(couponId, userId))
            throw new CouponIssueException(
                    ErrorCode.DUPLICATED_COUPON_ISSUE,
                    "이미 발급 요청이 처리 됐습니다. couponId=%s, userId=%s".formatted(couponId, userId));

        saveCouponIssue(couponId, userId);
    }

    private void saveCouponIssue(long couponId, long userId) {
        redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
    }

    private Boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) return true;
        return totalQuantity > redisRepository.sCard(getIssueRequestKey(couponId));
    }

    private Boolean availableUserIssueQuantity(long couponId, long userId) {
        return !redisRepository.sIsMember(getIssueRequestKey(couponId), String.valueOf(userId));
    }
}
