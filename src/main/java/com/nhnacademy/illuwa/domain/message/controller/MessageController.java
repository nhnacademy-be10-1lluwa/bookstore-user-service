package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageSendService messageSendService;

    //커스텀 메시지
    @PostMapping("api/messages/custom")
    public ResponseEntity<SendMessageResponse> sendCustomMessage(@RequestBody SendMessageRequest request) {
        return ResponseEntity.ok().body(messageSendService.sendDoorayMessage(request));
    }
}
