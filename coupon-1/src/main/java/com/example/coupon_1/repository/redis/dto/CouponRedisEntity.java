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
        boolean availableIssuedQuantity,
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
                coupon.availableIssuedQuantity(),
                coupon.getDateIssueStart(),
                coupon.getDateIssueEnd());
    }

    /**
     * (기존)
     * Coupon 엔티티와 달리 해당 record 는
     * issuedQuantity 필드가 없어서
     * 수량에 대한 유효성 검사를 할 수 없다.
     * 애초에 해당 record 는
     * 쿠폰 검색을 DB 에서 하지 않고
     * Redis 캐시로 조회하기 위해 만든 DTO 에 가까워서
     * issuedQuantity 처럼 실시간성 정보를 갖고 있기에 적절하지 않다
     * (변경)
     * 기존에는 위와 같이 issuedQuantity 를 관리하지 않았으나
     * 쿠폰 발급 수량을 모두 소진한 뒤 이벤트를 발행해서
     * 쿠폰에 대한 로컬 캐시와 Redis 캐시를 갱신하면서
     * 쿠폰의 issuedQuantity 도 갱신된다
     * 이를 CouponRedisEntity 에도 반영하기 위해
     * availableIssuedQuantity 필드가 추가되었고
     * availableIssuedQuantity 필드 정보는 생성자에서
     * 쿠폰의 availableIssuedQuantity 메소드로 갱신한다
     * availableIssuedQuantity 메소드는
     * totalQuantity 와 issuedQuantity 를 비교한다
     */
    public void checkIssuableCoupon() {
        if (!availableIssuedQuantity()) throw new CouponIssueException(
                INVALID_COUPON_ISSUE_QUANTITY,
                "모든 발급 수량이 소진 되었습니다. couponId: %s"
                        .formatted(id));

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
