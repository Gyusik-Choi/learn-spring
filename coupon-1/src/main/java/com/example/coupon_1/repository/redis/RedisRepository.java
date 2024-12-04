package com.example.coupon_1.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

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
}
