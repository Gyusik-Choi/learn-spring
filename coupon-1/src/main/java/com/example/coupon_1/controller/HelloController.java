package com.example.coupon_1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        // 초당 2건을 처리 * 200 (서버에서 동시에 처리할 수 있는 수 -> 톰캣 스레드 풀의 최대 스레드 갯수 200개) = 400
        // locust 로 성능 테스트를 하면 RPS 가 대략 400 이 나옴
        Thread.sleep(500);
        return "hello";
    }
}
