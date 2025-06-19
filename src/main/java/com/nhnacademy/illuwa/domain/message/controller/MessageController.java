package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageSendService messageSendService;

/*    //주문완료 메시지  _ TODO 주문번호 받아오는 것,,
    @PostMapping("/order")
    public ResponseEntity<Void> sendOrderMessage(@RequestBody SendMessageRequest request) {
        messageSendService.sendOrderMessage(request);
        return ResponseEntity.ok().build();
    }*/

    //커스텀 메시지
    @PostMapping("/custom")
    public ResponseEntity<Void> sendCustomMessage(@RequestBody SendMessageRequest request) {
        messageSendService.sendDoorayMessage(request);
        return ResponseEntity.ok().build();
    }
}
