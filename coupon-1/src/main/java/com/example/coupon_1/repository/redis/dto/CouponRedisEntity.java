package com.example.coupon_1.repository.redis.dto;

import com.example.coupon_1.exception.CouponIssueException;
import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.model.CouponType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

import static com.example.coupon_1.exception.ErrorCode.*;

public record CouponRedisEntity(
        Long id,
        CouponType couponType,
        Integer totalQuantity,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssueStart,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssueEnd
) {
    public CouponRedisEntity(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getCouponType(),
                coupon.getTotalQuantity(),
                coupon.getDateIssueStart(),
                coupon.getDateIssueEnd());
    }

    /**
     * Coupon 엔티티와 달리 해당 record 는
     * issuedQuantity 필드가 없어서
     * 수량에 대한 유효성 검사를 할 수 없다.
     * 애초에 해당 record 는
     * 쿠폰 검색을 DB 에서 하지 않고
     * Redis 캐시로 조회하기 위해 만든 DTO 에 가까워서
     * issuedQuantity 처럼 실시간성 정보를 갖고 있기에 적절하지 않다
     */
    public void checkIssuableCoupon() {
          if (!availableIssueDate()) throw new CouponIssueException(
                  INVALID_COUPON_ISSUE_DATE,
                  "발급 가능한 일자가 아닙니다. couponId: %s, issueStart: %s, issueEnd: %s"
                          .formatted(id, dateIssueStart(), dateIssueEnd()));
    }

    private boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart().isBefore(now) && dateIssueEnd().isAfter(now);
    }

}
