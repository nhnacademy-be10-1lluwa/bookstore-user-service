package com.nhnacademy.illuwa.domain.memberaddress.service;


import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberAddressService {
    /**
     * 주소 등록
     * - guest의 경우: guestId당 1개 주소만 등록 가능
     * - member의 경우: 기본 주소 존재 시 기존 기본설정 해제 후 등록, 최대 10개까지만 허용
     */
    MemberAddressResponse registerMemberAddress(long memberId, MemberAddressRequest request);
    /**
     * 주소 수정
     * - 기본 주소로 변경 요청이 들어올 경우, 기존 기본 주소는 해제됨
     */
    MemberAddressResponse updateMemberAddress(long memberId, long addressId, MemberAddressRequest request);

    /**
     * 주소 삭제
     */
    void deleteMemberAddress(long memberId, long addressId);

    /**
     * 주소 단건 조회
     */
    MemberAddressResponse getMemberAddress(long addressId);

    /**
     * member 주소 전체 조회
     */
    List<MemberAddressResponse> getMemberAddressList(long memberId);

    Page<MemberAddressResponse> getPagedMemberAddressList(long memberId, Pageable pageable);

    int countMemberAddress(long memberId);

    void setDefaultAddress(long memberId, long addressId);
}
