package com.example.coupon_1.service;

import com.example.coupon_1.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;

    /**
     * 1. 유저의 요청을 sorted set 에 적재 <br>
     * 2. 유저의 요청의 순서를 조회 <br>
     * 3. 조회 결과를 선착순 조건과 비교 <br>
     * 4. 쿠폰 발급 queue 에 적재
     */
    public void issue(long couponId, long userId) {
        String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());
    }
}
