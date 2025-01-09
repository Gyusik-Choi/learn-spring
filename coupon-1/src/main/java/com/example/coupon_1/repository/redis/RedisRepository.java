package com.example.coupon_1.repository.redis;

import com.example.coupon_1.controller.dto.CouponIssueRequestDto;
import com.example.coupon_1.exception.CouponIssueException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.coupon_1.exception.ErrorCode.FAIL_COUPON_ISSUE_REQUEST;
import static com.example.coupon_1.repository.redis.CouponIssueRequestCode.*;
import static com.example.coupon_1.util.CouponRedisUtil.*;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<String> issueScript = issueRequestScript();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * <a href="https://redis.io/docs/latest/commands/zadd/">zAdd</a><br>
     * 아래처럼 add, addIfAbsent 메소드를 호출하면 내부적으로 zadd 를 호출한다.<br>
     * zadd 의 시간 복잡도는 logN 이다.<br>
     * 선착순 쿠폰 이벤트에서는 전체 발급 수량이 중요하기 때문에
     * 첫번째부터 마지막 순서까지 우선 순위가 필요하지 않다.<br>
     * zadd 와 달리 sadd 의 경우 중복 요청의 경우 덮어쓰지 않고 무시된다.
     */
    public Boolean zAdd(String key, String value, double score) {
        // return redisTemplate.opsForZSet().add(key, value, score);
        // 위의 메서드는 이미 key 가 sorted set 에 존재하면 덮어쓴다
        // 여기서는 score 를 시간으로 넣기 때문에 동일한 key 에 대해서 중복 요청이 있을 경우
        // 기존 요청보다 이후 시간으로 score 를 덮어쓰기 때문에 순서가 뒤로 간다
        return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
    }

    public Long sAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long rPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public String lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    public String lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public void issueRequest(long couponId, long userId, long totalIssueQuantity) {
        String issueRequestKey = getIssueRequestKey(couponId);
        String issueRequestQueueKey = getIssueRequestQueueKey();
        CouponIssueRequestDto dto = new CouponIssueRequestDto(couponId, userId);
        try {
            String code = redisTemplate.execute(
                    // script
                    issueScript,
                    // keys (List 타입)
                    List.of(issueRequestKey, issueRequestQueueKey),
                    // args (나머지 모두)
                    String.valueOf(userId),
                    String.valueOf(totalIssueQuantity),
                    objectMapper.writeValueAsString(dto));
            checkRequestResult(find(code));
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(dto));
        }
    }

    /**
     * redis 의 쿠폰 요청 key 에 userId 가 존재하면 (이미 쿠폰 요청한 사용자인 경우) 2를 반환한다.
     * totalIssueQuantity 가 redis 의 쿠폰 요청 key 에 저장된 value 의 갯수 보다 크면
     * redis 의 쿠폰 요청 key 에 userId 를 추가하고,
     * redis 의 쿠폰 발급 key 에 쿠폰 발급 요청 정보를 추가하고,
     * 1을 반환한다.
     * 그 외의 경우는 3을 반환한다.
     */
    private RedisScript<String> issueRequestScript() {
        String script =
                """
                if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then
                    return '2'
                end
                
                if tonumber(ARGV[2]) > redis.call('SCARD', KEYS[1]) then
                    redis.call('SADD', KEYS[1], ARGV[1])
                    redis.call('RPUSH', KEYS[2], ARGV[3])
                    return '1'
                end
                
                return '3'
                """;
        return RedisScript.of(script, String.class);
    }
}
