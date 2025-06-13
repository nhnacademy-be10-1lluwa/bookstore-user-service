package com.nhnacademy.illuwa.domain.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InactiveVerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean verifyCode(String email, String inputCode) {
        String key = "verify:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        return inputCode != null && inputCode.equals(storedCode);
    }
}

