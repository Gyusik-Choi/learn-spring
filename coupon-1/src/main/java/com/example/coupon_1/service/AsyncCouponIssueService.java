package com.example.coupon_1.service;

import com.example.coupon_1.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.coupon_1.util.CouponRedisUtil.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;

    /**
     * 유저의 요청을 sorted set 에 적재
     */
    public void issueV1(long couponId, long userId) {
        String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());
    }

    public void issueV2(long couponId, long userId) {

    }

    public Boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) return true;
        return totalQuantity > redisRepository.sCard(getIssueRequestKey(couponId));
    }

    public Boolean availableUserIssueQuantity(long couponId, long userId) {
        return !redisRepository.sIsMember(getIssueRequestKey(couponId), String.valueOf(userId));
    }
}
