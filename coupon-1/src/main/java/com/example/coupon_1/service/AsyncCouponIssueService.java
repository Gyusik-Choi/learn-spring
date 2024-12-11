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

        /*
          distributeLockExecutor 분산락 안에서 진행되는 로직
          1. totalQuantity > redisRepository.sCard(key); // 쿠폰 발급 수량 제어
          2. !redisRepository.sIsMember(key, String.valueOf(userId)); // 중복 발급 요청 제어
          3. redisRepository.sAdd // 쿠폰 발급 요청 저장
          4. redisRepository.rPush // 쿠폰 발급 큐 적재

          https://tjdrnr05571.tistory.com/18
          (distributeLockExecutor) 레디스 분산락을 사용하지 않으면 동시성 이슈가 일어날 수 있디
          레디스가 싱글 스레드로 동작하는데 동시성 이슈가 왜 발생할 수 있는지 궁금했는데 위 글이 이를 이해하는데 도움이 됐다
          단일 연산이라면 분산락 없이 동시성 이슈가 발생하지 않았겠지만 여러 연산이 수행되기 때문에
          싱글 스레드라고 하더라도 동시성 이슈가 발생할 수 있다
          위의 1, 2 연산에서 정합성을 검증하고 3, 4 연산에서 쿠폰 발급을 처리한다
          특정 스레드의 1, 2 연산과 3, 4 연산 사이에 다른 여러 스레드들이 1, 2 연산을 수행하게 되면
          1, 2 연산은 모두 통과하게 돼서 3, 4 연산도 문제없이 발생하게 된다
          그래서 동시성 이슈가 생길 수 있다

          -> 성능 개선 및 동시성 이슈 개선을 위해 distributeLockExecutor 대신
          redis script 를 통해 4개 작업을 원자성을 가진 1개 작업으로 전환
         */
        distributeLockExecutor.execute("lock %s".formatted(couponId), 3000, 3000, () -> {
            asyncCouponValidator.checkAvailableCouponIssue(coupon, userId);
            saveCouponIssue(couponId, userId);
        });
    }

    public void issueV5(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);

        coupon.checkIssuableCoupon();

        int totalQuantity = coupon.totalQuantity() == null
                ? Integer.MAX_VALUE
                : coupon.totalQuantity();

        redisRepository.issueRequest(couponId, userId, totalQuantity);
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
