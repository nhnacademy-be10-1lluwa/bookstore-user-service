package com.nhnacademy.illuwa.domain.memberaddress.controller;

import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.service.MemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members/addresses")
@Tag(name = "회원 주소 API", description = "회원 주소 등록, 조회, 수정, 삭제, 기본 설정 API")
public class MemberAddressController {
    private final MemberAddressService memberAddressService;

    @Operation(summary = "회원 주소 개수 조회", description = "회원이 등록한 주소의 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "주소 개수 반환 성공")
    @ApiResponse(responseCode = "400", description = "요청 오류")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @GetMapping("/count")
    public ResponseEntity<Integer> getMemberAddressCount(@RequestHeader("X-USER-ID") long memberId){
        int count = memberAddressService.countMemberAddress(memberId);
        return ResponseEntity.ok().body(count);
    }

    @Operation(summary = "회원 주소 전체 조회", description = "회원이 등록한 모든 주소를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "주소 목록 조회 성공")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @GetMapping
    public ResponseEntity<List<MemberAddressResponse>> getMemberAddressList(@RequestHeader("X-USER-ID") long memberId){
        return ResponseEntity.ok().body(memberAddressService.getMemberAddressList(memberId));
    }

    @Operation(summary = "회원 주소 페이징 조회", description = "회원 주소 목록을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주소 페이징 조회 성공")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @GetMapping("/paged")
    public ResponseEntity<Page<MemberAddressResponse>> getPagedMemberAddressList(
            @RequestHeader("X-USER-ID") long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("defaultAddress").descending());
        Page<MemberAddressResponse> addressPage = memberAddressService.getPagedMemberAddressList(memberId, pageable);

        return ResponseEntity.ok(addressPage);
    }

    @Operation(summary = "회원 주소 등록", description = "새로운 회원 주소를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "주소 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping
    public ResponseEntity<MemberAddressResponse> createMemberAddress(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberAddressRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberAddressService.registerMemberAddress(memberId, request));
    }

    @Operation(summary = "회원 주소 단건 조회", description = "주소 ID로 회원 주소를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주소 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 주소 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @GetMapping("/{addressId}")
    public ResponseEntity<MemberAddressResponse> getMemberAddress(@PathVariable long addressId){
        return ResponseEntity.ok().body(memberAddressService.getMemberAddress(addressId));
    }

    @Operation(summary = "회원 주소 수정", description = "기존 주소 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "주소 수정 성공")
    @ApiResponse(responseCode = "400", description = "유효성 오류")
    @ApiResponse(responseCode = "404", description = "해당 주소 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/{addressId}")
    public ResponseEntity<MemberAddressResponse> updateMemberAddress(@RequestHeader("X-USER-ID") long memberId, @PathVariable long addressId, @Valid @RequestBody MemberAddressRequest request){
        return ResponseEntity.ok().body(memberAddressService.updateMemberAddress(memberId, addressId, request));
    }

    @Operation(summary = "회원 주소 삭제", description = "주소 ID로 회원 주소를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 주소 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteMemberAddress(@RequestHeader("X-USER-ID") long memberId, @PathVariable long addressId){
        memberAddressService.deleteMemberAddress(memberId, addressId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기본 주소 설정", description = "선택한 주소를 기본 주소로 설정합니다.")
    @ApiResponse(responseCode = "200", description = "기본 주소 설정 성공")
    @ApiResponse(responseCode = "404", description = "해당 주소 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PutMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@RequestHeader("X-USER-ID") long memberId, @PathVariable long addressId){
        memberAddressService.setDefaultAddress(memberId, addressId);
        return ResponseEntity.ok().build();
    }
}