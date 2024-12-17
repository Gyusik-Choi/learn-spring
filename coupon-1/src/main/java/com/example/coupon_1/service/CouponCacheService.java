package com.example.coupon_1.service;

import com.example.coupon_1.model.Coupon;
import com.example.coupon_1.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {

    private final CouponIssueService couponIssueService;

    @Cacheable(cacheNames = "coupon")
    public CouponRedisEntity getCouponCache(long couponId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

    /**
     * 기본적으로 로컬 캐시에서 조회하는데
     * 로컬 캐시에 해당하는 쿠폰이 없으면 Redis 캐시에서 읽어온다
     */
    @Cacheable(cacheNames = "coupon", cacheManager = "localCacheManager")
    public CouponRedisEntity getCouponLocalCache(long couponId) {
        // return getCouponCache(couponId);
        // 위처럼 바로 getCouponCache 를 호출하면
        // @Cacheable 애노테이션이 동작하지 못한다
        // @Cacheable 애노테이션이 동작하려면 프록시를 통해서 호출해야 한다
        // 바로 getCouponCache 를 호출하면 단지 메소드간의 내부 호출이 된다
        // 스프링이 생성하는 CouponCacheService 프록시가
        // @Cacheable 애노테이션과 관련된 처리를 할 수 없게된다
        // 프록시가 @Cacheable 애노테이션 관련 처리를 할 수 있도록
        // getCouponCache 메소드를 직접 호출하지 않고
        // 프록시를 통해서 getCouponCache 메소드를 호출하도록 한다
        return proxy().getCouponCache(couponId);
    }

    private CouponCacheService proxy() {
        return ((CouponCacheService) AopContext.currentProxy());
    }

}
