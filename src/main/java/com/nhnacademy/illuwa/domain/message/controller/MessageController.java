package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageSendService messageSendService;

    //비회원 주문완료 메시지
    @PostMapping("/order")
    public ResponseEntity<SendMessageResponse> sendOrderMessage(@RequestBody GuestOrderRequest request) {
        return ResponseEntity.ok().body(messageSendService.sendOrderMessage(request));
    }

    //커스텀 메시지
    @PostMapping("/custom")
    public ResponseEntity<SendMessageResponse> sendCustomMessage(@RequestBody SendMessageRequest request) {
        return ResponseEntity.ok().body(messageSendService.sendDoorayMessage(request));
    }
}
