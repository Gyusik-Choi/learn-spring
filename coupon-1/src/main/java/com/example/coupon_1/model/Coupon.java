package com.example.coupon_1.model;

import com.example.coupon_1.exception.CouponIssueException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.coupon_1.exception.ErrorCode.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    @Column()
    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    public void issue() {
        checkIssuableCoupon();
        issuedQuantity += 1;
    }

    public void checkIssuableCoupon() {
        if (!availableIssuedQuantity()) {
            throw new CouponIssueException(
                    INVALID_COUPON_ISSUE_QUANTITY,
                    "발급 가능한 수량을 초과 합니다. total : %s, issued: %s"
                            .formatted(totalQuantity, issuedQuantity));
        }

        if (!availableIssueDate()) {
            throw new CouponIssueException(
                    INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 일자가 아닙니다. request : %s, issueStart : %s, issueEnd : %s"
                            .formatted(LocalDateTime.now(), dateIssueEnd, dateIssueStart));
        }
    }

    public boolean availableIssuedQuantity() {
        if (totalQuantity == null) return true;
        return totalQuantity > issuedQuantity;
    }

    private boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        if (dateIssueStart == null || dateIssueEnd == null) return true;
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    public boolean isIssueComplete() {
        return dateIssueEnd.isBefore(LocalDateTime.now()) || !availableIssuedQuantity();
    }
}
