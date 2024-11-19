package com.example.coupon_1.repository.mysql;

import com.example.coupon_1.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
