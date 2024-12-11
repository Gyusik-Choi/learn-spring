package com.example.coupon_1.service;

import com.example.coupon_1.component.DistributeLockExecutor;
import com.example.coupon_1.controller.dto.CouponIssueRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final AsyncCouponIssueService asyncCouponIssueService;

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }

    /**
     * synchronized - 1
     *
     * 실행 순서 <br>
     * 1. 트랜잭션 시작 <br>
     * 2. lock 획득 <br>
     * 3. issue() <br>
     * 4. lock 반납 <br>
     * 5. 트랜잭션 커밋 <br>
     * -> <br>
     * 한 스레드가 4를 마치고 5를 끝내기 전에
     * 다른 스레드가 lock 을 획득해서 진행할 수 있다. <br>
     * 다른 스레드는 쿠폰 수량을 조회하면 아직 쿠폰 수량이 DB 에 1 올라가기 이전의 값이 조회된다
     */
    public void issueRequestV2(CouponIssueRequestDto requestDto) {
        couponIssueService.issueWithLock(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }

    /**
     * synchronized - 2
     *
     * 실행 순서 <br>
     * 1. lock 획득 <br>
     * 2. 트랜잭션 시작<br>
     * 3. issue() <br>
     * 4. 트랜잭션 커밋 <br>
     * 5. lock 반납 <br>
     * -> <br>
     * issueRequestV2 의 동시성 이슈를 해결하기 위해
     * 트랜잭션 보다 락을 먼저 획득해서 실행할 수 있다
     */
    public void issueRequestV3(CouponIssueRequestDto requestDto) {
        synchronized (this) {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
            log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
        }
    }

    /**
     * redis 분산락 (redisson)
     */
    public void issueRequestV4(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute(
                "lock_" + requestDto.couponId(),
                10000,
                10000,
                () -> couponIssueService.issue(requestDto.couponId(), requestDto.userId()));
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }

    /**
     * DB 락 - exclusive lock
     */
    public void issueRequestV5(CouponIssueRequestDto requestDto) {
        couponIssueService.issueWithXLock(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }

    /**
     * 유저의 요청을 sorted set 에 적재
     */
    public void asyncIssueRequestV1(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueV1(requestDto.couponId(), requestDto.userId());
    }

    /**
     * AsyncCouponIssueService 의 issueV2 메소드 연산들이 원자적이지 않아서 동시성 이슈가 발생할 수 있다.
     */
    public void asyncIssueRequestV2(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueV2(requestDto.couponId(), requestDto.userId());
    }

    /**
     * Redis 분산락
     */
    public void asyncIssueRequestV3(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueV3(requestDto.couponId(), requestDto.userId());
    }

    /**
     * Redis 분산락 + 쿠폰을 DB 가 아닌 Redis 캐시에서 조회
     */
    public void asyncIssueRequestV4(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueV4(requestDto.couponId(), requestDto.userId());
    }

    public void asyncIssueRequestV5(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueV5(requestDto.couponId(), requestDto.userId());
    }
}
