package com.example.coupon_1.component;

import com.example.coupon_1.controller.dto.CouponIssueRequestDto;
import com.example.coupon_1.repository.redis.RedisRepository;
import com.example.coupon_1.service.CouponIssueService;
import com.example.coupon_1.util.CouponRedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
public class CouponIssueListener {

    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;
    private final String issueRequestQueueKey = CouponRedisUtil.getIssueRequestQueueKey();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 1000L)
    public void issue() throws JsonProcessingException {
        while (existCouponIssueTarget()) {
            CouponIssueRequestDto target = getIssueTarget();
            log.info("발급 시작 target: %s".formatted(target));
            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 완료 target: %s".formatted(target));
            removeIssuedTarget();
        }
    }

    private boolean existCouponIssueTarget() {
        return redisRepository.lSize(issueRequestQueueKey) > 0;
    }

    private CouponIssueRequestDto getIssueTarget() throws JsonProcessingException {
        return objectMapper.readValue(
                redisRepository.lIndex(issueRequestQueueKey, 0),
                CouponIssueRequestDto.class);
    }

    private void removeIssuedTarget() {
        redisRepository.lPop(issueRequestQueueKey);
    }
}
