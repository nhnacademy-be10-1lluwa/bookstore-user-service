package com.nhnacademy.illuwa.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "doorayMessageClient", url = "${spring.dooray.webhook.url}")
public interface DoorayMessageClient {
    @PostMapping
    void sendMessage(@RequestBody Map<String, Object> messageBody);
}
