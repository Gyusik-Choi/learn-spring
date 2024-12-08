package com.example.coupon_1.util;

public class CouponRedisUtil {

    public static String getIssueRequestKey(long couponId) {
        return "issue.request.couponId=%s".formatted(couponId);
    }

    public static String getIssueRequestQueueKey() {
        return "issue.request.queue.key";
    }
}