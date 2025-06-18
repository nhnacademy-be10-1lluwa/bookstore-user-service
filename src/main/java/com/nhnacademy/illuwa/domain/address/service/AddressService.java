package com.nhnacademy.illuwa.domain.address.service;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;

import java.util.List;

public interface AddressService {
    /**
     * 주소 등록
     * - guest의 경우: guestId당 1개 주소만 등록 가능
     * - member의 경우: 기본 주소 존재 시 기존 기본설정 해제 후 등록, 최대 10개까지만 허용
     */
    AddressResponse registerAddressForMember(long memberId, AddressRequest request);

    AddressResponse registerAddressForGuest(long guestId, AddressRequest request);

    /**
     * 주소 수정
     * - 기본 주소로 변경 요청이 들어올 경우, 기존 기본 주소는 해제됨
     */
    AddressResponse updateAddress(long addressId, AddressRequest request);

    /**
     * 주소 삭제
     */
    void deleteAddress(long addressId);

    /**
     * 주소 단건 조회
     */
    AddressResponse getAddress(long addressId);

    /**
     * member 주소 전체 조회
     */
    List<AddressResponse> getAddressesByMember(long memberId);

    /**
     * guest 주소 조회 (1개만 존재)
     */
    AddressResponse getAddressByGuest(long guestId);

}
