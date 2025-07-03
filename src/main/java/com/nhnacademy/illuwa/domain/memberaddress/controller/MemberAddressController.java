package com.nhnacademy.illuwa.domain.memberaddress.controller;

import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressLimitResponse;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.service.MemberAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members/addresses")
public class MemberAddressController {
    private final MemberAddressService memberAddressService;

    @GetMapping("/check-limit")
    public ResponseEntity<MemberAddressLimitResponse> checkAddressLimit(@RequestHeader("X-USER-ID") long memberId){
        int count = memberAddressService.countMemberAddress(memberId);
        boolean canAdd = count < 10;

        MemberAddressLimitResponse response = MemberAddressLimitResponse.builder()
                .canAdd(canAdd)
                .currentCount(count)
                .maxLimit(10)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<MemberAddressResponse>> getMemberAddressList(@RequestHeader("X-USER-ID") long memberId){
        return ResponseEntity.ok().body(memberAddressService.getMemberAddressList(memberId));
    }

    @PostMapping
    public ResponseEntity<MemberAddressResponse> createMemberAddress(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberAddressRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberAddressService.registerMemberAddress(memberId, request));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<MemberAddressResponse> getMemberAddress(@RequestHeader("X-USER-ID") long addressId){
        return ResponseEntity.ok().body(memberAddressService.getMemberAddress(addressId));
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<MemberAddressResponse> updateMemberAddress(@PathVariable long addressId, @Valid @RequestBody MemberAddressRequest request){
        return ResponseEntity.ok().body(memberAddressService.updateMemberAddress(addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteMemberAddress(@PathVariable long addressId){
        memberAddressService.deleteMemberAddress(addressId);
        return ResponseEntity.ok().build();
    }
}
