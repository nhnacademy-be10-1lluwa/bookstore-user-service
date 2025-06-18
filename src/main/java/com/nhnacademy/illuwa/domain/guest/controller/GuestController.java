package com.nhnacademy.illuwa.domain.guest.controller;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/guests")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    @PostMapping("/login")
    public ResponseEntity<GuestResponse> login(@Valid @RequestBody GuestLoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(guestService.login(request));
    }

}
