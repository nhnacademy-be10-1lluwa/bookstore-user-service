package com.nhnacademy.illuwa.domain.memberaddress.controller;

import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.service.MemberAddressService;
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
public class MemberAddressController {
    private final MemberAddressService memberAddressService;

    @GetMapping("/count")
    public ResponseEntity<Integer> getMemberAddressCount(@RequestHeader("X-USER-ID") long memberId){
        int count = memberAddressService.countMemberAddress(memberId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping
    public ResponseEntity<List<MemberAddressResponse>> getMemberAddressList(@RequestHeader("X-USER-ID") long memberId){
        return ResponseEntity.ok().body(memberAddressService.getMemberAddressList(memberId));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<MemberAddressResponse>> getPagedMemberAddressList(
            @RequestHeader("X-USER-ID") long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("defaultAddress").descending());
        Page<MemberAddressResponse> addressPage = memberAddressService.getPagedMemberAddressList(memberId, pageable);

        return ResponseEntity.ok(addressPage);
    }

    @PostMapping
    public ResponseEntity<MemberAddressResponse> createMemberAddress(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberAddressRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberAddressService.registerMemberAddress(memberId, request));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<MemberAddressResponse> getMemberAddress(@PathVariable long addressId){
        return ResponseEntity.ok().body(memberAddressService.getMemberAddress(addressId));
    }

    @PostMapping("/{addressId}")
    public ResponseEntity<MemberAddressResponse> updateMemberAddress(@RequestHeader("X-USER-ID") long memberId, @PathVariable long addressId, @Valid @RequestBody MemberAddressRequest request){
        return ResponseEntity.ok().body(memberAddressService.updateMemberAddress(memberId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteMemberAddress(@RequestHeader("X-USER-ID") long memberId, @PathVariable long addressId){
        memberAddressService.deleteMemberAddress(memberId, addressId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(@RequestHeader("X-USER-ID") long memberId, @PathVariable long addressId){
        memberAddressService.setDefaultAddress(memberId, addressId);
        return ResponseEntity.ok().build();
    }
}
