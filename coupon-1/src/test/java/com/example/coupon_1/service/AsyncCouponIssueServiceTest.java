package com.example.coupon_1.service;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.model.CouponType;
import com.example.coupon_1.repository.mysql.CouponJpaRepository;
import com.example.coupon_1.repository.redis.dto.CouponIssueQueueValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.IntStream;

import static com.example.coupon_1.util.CouponRedisUtil.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AsyncCouponIssueServiceTest {

    @Autowired
    AsyncCouponIssueService sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외를 반환한다")
    void issueV3_1() {
        // given
        long couponId = 1;
        long userId = 1;

        // when & then
        assertThatThrownBy(() -> sut.issueV3(couponId, userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다")
    void issueV3_2() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // 쿠폰 발급 가능 수량이 다 찬 경우를 만들기 위해
        // Redis 에 쿠폰 발급 요청을
        // 위에서 만든 쿠폰의 totalQuantity 만큼 채워 놓는다
        IntStream
                .range(0, coupon.getTotalQuantity())
                .forEach(user ->
                        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(user)));

        // when & then
        assertThatThrownBy(() -> sut.issueV3(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
    void issueV3_3() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        // when & then
        assertThatThrownBy(() -> sut.issueV3(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기한이 유효하지 않다면 예외를 반환한다")
    void issueV3_4() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        // when & then
        assertThatThrownBy(() -> sut.issueV3(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다")
    void issueV3_5() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        sut.issueV3(coupon.getId(), userId);

        // then
        Boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        assertThat(isSaved).isTrue();
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 요청이 성공하면 쿠폰 발급 큐에 적재된다")
    void issueV3_6() throws JsonProcessingException {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        CouponIssueQueueValue queueValue = new CouponIssueQueueValue(coupon.getId(), userId);

        // when
        sut.issueV3(coupon.getId(), userId);

        // then
        String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());

        assertThat(new ObjectMapper().writeValueAsString(queueValue)).isEqualTo(savedIssueRequest);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외를 반환한다")
    void issueV5_1() {
        // given
        long couponId = 1;
        long userId = 1;

        // when & then
        assertThatThrownBy(() -> sut.issueV5(couponId, userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다")
    void issueV5_2() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // 쿠폰 발급 가능 수량이 다 찬 경우를 만들기 위해
        // Redis 에 쿠폰 발급 요청을
        // 위에서 만든 쿠폰의 totalQuantity 만큼 채워 놓는다
        IntStream
                .range(0, coupon.getTotalQuantity())
                .forEach(user ->
                        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(user)));

        // when & then
        assertThatThrownBy(() -> sut.issueV5(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
    void issueV5_3() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        // when & then
        assertThatThrownBy(() -> sut.issueV5(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기한이 유효하지 않다면 예외를 반환한다")
    void issueV5_4() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        // when & then
        assertThatThrownBy(() -> sut.issueV5(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다")
    void issueV5_5() {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        sut.issueV5(coupon.getId(), userId);

        // then
        Boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        assertThat(isSaved).isTrue();
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 요청이 성공하면 쿠폰 발급 큐에 적재된다")
    void issueV5_6() throws JsonProcessingException {
        // given
        long userId = 1;

        // 쿠폰을 DB 에 저장
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        CouponIssueQueueValue queueValue = new CouponIssueQueueValue(coupon.getId(), userId);

        // when
        sut.issueV5(coupon.getId(), userId);

        // then
        String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());

        assertThat(new ObjectMapper().writeValueAsString(queueValue)).isEqualTo(savedIssueRequest);
    }
}
