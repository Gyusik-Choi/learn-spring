package com.example.coupon_1.service;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.model.CouponIssue;
import com.example.coupon_1.model.CouponType;
import com.example.coupon_1.repository.mysql.CouponIssueJpaRepository;
import com.example.coupon_1.repository.mysql.CouponIssueRepository;
import com.example.coupon_1.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.coupon_1.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest()
class CouponIssueServiceTest {

    @Autowired
    CouponIssueService sut;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @BeforeEach
    void clear() {
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환한다")
    void saveCouponIssue_1() {
        // given
        CouponIssue couponIssue = CouponIssue
                .builder()
                .couponId(1L)
                .userId(1L)
                .build();

        couponIssueJpaRepository.save(couponIssue);

        // when
        CouponIssueException exception = assertThrows(CouponIssueException.class, () ->
                sut.saveCouponIssue(couponIssue.getCouponId(), couponIssue.getUserId()));

        // then
        assertEquals(DUPLICATED_COUPON_ISSUE, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 발급 내역이 존재하지 않는다면 쿠폰을 발급한다")
    void saveCouponIssue_2() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when
        CouponIssue result = sut.saveCouponIssue(couponId, userId);

        // then
        assertTrue(couponIssueJpaRepository.findById(result.getId()).isPresent());
    }

    @Test
    @DisplayName("발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급한다")
    void issue_1() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon
                .builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        sut.issue(coupon.getId(), userId);

        // then
        Coupon couponResult = couponJpaRepository.findById(coupon.getId()).get();
        assertEquals(1, couponResult.getIssuedQuantity());

        CouponIssue couponIssueResult = couponIssueRepository.findFirstCouponIssue(coupon.getId(), userId);
        assertNotNull(couponIssueResult);
    }

    @Test
    @DisplayName("발급 수량에 문제가 있다면 예외를 반환한다")
    void issue_2() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon
                .builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(coupon.getId(), userId));
        assertEquals(INVALID_COUPON_ISSUE_QUANTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("발급 기한에 문제가 있다면 예외를 반환한다")
    void issue_3() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon
                .builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(coupon.getId(), userId));
        assertEquals(INVALID_COUPON_ISSUE_DATE, exception.getErrorCode());
    }

    @Test
    @DisplayName("중복 발급 검증에 문제가 있다면 예외를 반환한다")
    void issue_4() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon
                .builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        CouponIssue couponIssue = CouponIssue
                .builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();

        couponIssueJpaRepository.save(couponIssue);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(coupon.getId(), userId));
        assertEquals(DUPLICATED_COUPON_ISSUE, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰이 존재하지 않는다면 예외를 반환한다")
    void issue_5() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(couponId, userId));
        assertEquals(COUPON_NOT_EXIST, exception.getErrorCode());
    }
}
