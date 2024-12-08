package com.example.coupon_1.service;

import com.example.coupon_1.component.DistributeLockExecutor;
import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.repository.redis.RedisRepository;
import com.example.coupon_1.repository.redis.dto.CouponIssueQueueValue;
import com.example.coupon_1.repository.redis.dto.CouponRedisEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.coupon_1.exception.ErrorCode.*;
import static com.example.coupon_1.util.CouponRedisUtil.getIssueRequestKey;
import static com.example.coupon_1.util.CouponRedisUtil.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final AsyncCouponIssueValidator asyncCouponValidator;
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper mapper = new ObjectMapper();
    private final CouponCacheService couponCacheService;

    /**
     * 유저의 요청을 sorted set 에 적재
     */
    public void issueV1(long couponId, long userId) {
        String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());
    }

    /**
     * 아래의 연산들이 원자적이지 않아서 동시성 이슈가 발생할 수 있다.
     */
    public void issueV2(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        coupon.checkIssuableCoupon();

        asyncCouponValidator.checkAvailableCouponIssue(coupon, userId);

        saveCouponIssue(couponId, userId);
    }

    /**
     * Redis 분산락
     */
    public void issueV3(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        coupon.checkIssuableCoupon();

        distributeLockExecutor.execute("lock %s".formatted(couponId), 3000, 3000, () -> {
            asyncCouponValidator.checkAvailableCouponIssue(coupon, userId);
            saveCouponIssue(couponId, userId);
        });
    }

    /**
     * 쿠폰을 DB 가 아닌 Redis 캐시에서 조회
     */
    public void issueV4(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);

        coupon.checkIssuableCoupon();

        distributeLockExecutor.execute("lock %s".formatted(couponId), 3000, 3000, () -> {
            asyncCouponValidator.checkAvailableCouponIssue(coupon, userId);
            saveCouponIssue(couponId, userId);
        });
    }

    private void saveCouponIssue(long couponId, long userId) {
        CouponIssueQueueValue queueValue = new CouponIssueQueueValue(couponId, userId);
        String queueStringValue;
        try {
            queueStringValue = mapper.writeValueAsString(queueValue);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(queueValue));
        }

        // 쿠폰 수량 관리 set 에 적재
        redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
        // 쿠폰 발급 대기열(큐)에 적재
        redisRepository.rPush(getIssueRequestQueueKey(), queueStringValue);
    }
}
