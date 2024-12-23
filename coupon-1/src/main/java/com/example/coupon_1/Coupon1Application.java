package com.example.coupon_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy = true)
public class Coupon1Application {

	public static void main(String[] args) {
		SpringApplication.run(Coupon1Application.class, args);
	}

}
