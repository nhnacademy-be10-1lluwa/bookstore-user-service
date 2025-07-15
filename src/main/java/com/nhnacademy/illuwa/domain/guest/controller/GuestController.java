package com.nhnacademy.illuwa.domain.guest.controller;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/guests")
public class GuestController {
    private final GuestService guestService;

    @PostMapping("/create")
    public ResponseEntity<GuestResponse> create(@Valid @RequestBody GuestOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.createGuest(request));
    }

    @PostMapping
    public ResponseEntity<GuestResponse> getGuest(@Valid @RequestBody GuestLoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(guestService.getGuest(request));
    }
}
