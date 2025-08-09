package com.nhnacademy.illuwa.domain.grade.service;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class IdemService {
    private final RedisTemplate<String, Object> redis;
    private static final long TTL = 60 * 60 * 24;

    public <T> T run(String key, Supplier<T> action) {
        boolean first = Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, "LOCK", TTL, TimeUnit.SECONDS));

        if(!first) {
            // 이미 처리 -> 캐시된 응답 or 409
            Object cached = redis.opsForValue().get(key+":resp");
            if(cached != null) {
                return (T) cached;
            }
            throw new DuplicateRequestException();
        }

        T result = action.get();
        redis.opsForValue().set(key+":resp", result, TTL, TimeUnit.SECONDS);
        return result;
    }
}
