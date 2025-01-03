package com.example.coupon_1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.example.coupon_1.util.CouponRedisUtil.getIssueRequestKey;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AsyncCouponIssueValidatorTest {

    @Autowired
    AsyncCouponIssueValidator sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true 를 반환한다")
    void availableTotalIssueQuantity_1() {
        // given
        int totalIssueQuantity = 10;
        long couponId = 1L;

        // when
        boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 모두 소진되면 false 를 반환한다")
    void availableTotalIssueQuantity_2() {
        // given
        int totalIssueQuantity = 10;
        long couponId = 1L;
        IntStream
                .range(0, totalIssueQuantity)
                .forEach(userId -> redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId)));

        // when
        boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        Assertions.assertFalse(result);

    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true 를 반환한다")
    void availableUserIssueQuantity_1() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when
        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false 를 반환한다")
    void availableUserIssueQuantity_2() {
        // given
        long couponId = 1L;
        long userId = 1L;
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        // when
        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        // then
        Assertions.assertFalse(result);
    }
}
