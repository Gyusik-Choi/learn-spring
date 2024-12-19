package com.example.coupon_1.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class CacheConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException:
     * No qualifying bean of type 'org.springframework.cache.CacheManager' available:
     * expected single matching bean but found 2: redisCacheManager,localCacheManager
     *
     * java.lang.IllegalStateException: No CacheResolver specified, and no unique bean of type CacheManager found.
     * Mark one as primary or declare a specific CacheManager to use.
     *
     * 캐시 매니저가 redisCacheManager, localCacheManager 2개가 등록됐기 때문에 우선 순위를 적용해야 한다
     * redisCacheManager 에 @Primary 애노테이션을 적용했다
     */
    @Bean
    @Primary
    public CacheManager redisCacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(30));
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
