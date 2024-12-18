package com.example.coupon_1.model;

import com.example.coupon_1.exception.CouponIssueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.example.coupon_1.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아있다면 true 를 반환한다")
    void availableIssuedQuantity_1() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        // when & then
        assertDoesNotThrow(coupon::checkIssuableCoupon);
    }

    @Test
    @DisplayName("발급 수량이 남아있지 않다면 false 를 반환한다")
    void availableIssuedQuantity_2() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        // when & then
        assertThrows(CouponIssueException.class, coupon::checkIssuableCoupon);
    }

    @Test
    @DisplayName("최대 발급 수량이 설정되지 않았다면 true 를 반환한다")
    void availableIssuedQuantity_3() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(null)
                .issuedQuantity(100)
                .build();

        // when & then
        assertDoesNotThrow(coupon::checkIssuableCoupon);
    }

    @Test
    @DisplayName("발급 기간이 시작되지 않았다면 false 를 반환한다")
    void availableIssueDate_1() {
        // given
        Coupon coupon = Coupon
                .builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        assertThrows(CouponIssueException.class, coupon::checkIssuableCoupon);
    }

    @Test
    @DisplayName("발급 기간에 해당되면 true 를 반환한다")
    void availableIssueDate_2() {
        // given
        Coupon coupon = Coupon
                .builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        assertDoesNotThrow(coupon::checkIssuableCoupon);
    }

    @Test
    @DisplayName("발급 기간이 종료되면 false 를 반환한다")
    void availableIssueDate_3() {
        // given
        Coupon coupon = Coupon
                .builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        // when & then
        assertThrows(CouponIssueException.class, coupon::checkIssuableCoupon);
    }

    @Test
    @DisplayName("발급 수량과 발급 기간이 유효하면 발급에 성공한다")
    void issue_1() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        coupon.issue();

        // then
        assertEquals(100, coupon.getIssuedQuantity());
    }

    @Test
    @DisplayName("발급 수량을 초과하면 예외를 반환한다")
    void issue_2() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, coupon::issue);
        assertEquals(INVALID_COUPON_ISSUE_QUANTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("발급 기간이 유효하지 않으면 예외를 반환한다")
    void issue_3() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, coupon::issue);
        assertEquals(INVALID_COUPON_ISSUE_DATE, exception.getErrorCode());
    }

    @Test
    @DisplayName("발급 기한이 종료되면 True 를 반환한다")
    void isIssueComplete_1() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        // when
        boolean result = coupon.isIssueComplete();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("잔여 발급 가능 수량이 없다면 True 를 반환한다")
    void isIssueComplete_2() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        // when
        boolean result = coupon.isIssueComplete();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한과 수량이 유효하면 false 를 반환한다")
    void isIssueComplete_3() {
        // given
        Coupon coupon = Coupon
                .builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        // when
        boolean result = coupon.isIssueComplete();

        // then
        assertFalse(result);
    }
}
