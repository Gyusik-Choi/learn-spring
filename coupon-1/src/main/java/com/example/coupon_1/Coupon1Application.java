package com.example.coupon_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Coupon1Application {

	public static void main(String[] args) {
		SpringApplication.run(Coupon1Application.class, args);
	}

}
