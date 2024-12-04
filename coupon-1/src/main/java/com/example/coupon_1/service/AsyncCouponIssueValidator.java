package com.example.coupon_1.service;

import com.example.coupon_1.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
