package com.nhnacademy.illuwa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> StringRedisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);

        StringRedisSerializer keySer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSer = new GenericJackson2JsonRedisSerializer();

        redisTemplate.setKeySerializer(keySer);
        redisTemplate.setValueSerializer(valueSer);
        redisTemplate.setHashKeySerializer(keySer);
        redisTemplate.setHashValueSerializer(valueSer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }
}
