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
