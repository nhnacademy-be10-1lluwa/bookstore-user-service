package com.nhnacademy.illuwa.domain.guest.controller;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/guests")
@Tag(name = "비회원 API", description = "비회원 정보 생성, 조회 관련 API")
public class GuestController {
    private final GuestService guestService;

    @ApiResponse(responseCode = "201", description = "비회원 생성 성공")
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/create")
    public ResponseEntity<GuestResponse> create(@Valid @RequestBody GuestOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.createGuest(request));
    }

    @ApiResponse(responseCode = "200", description = "비회원 조회 성공")
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음")
    @ApiResponse(responseCode = "404", description = "비회원 정보를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping
    public ResponseEntity<GuestResponse> getGuest(@Valid @RequestBody GuestLoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(guestService.getGuest(request));
    }
}
