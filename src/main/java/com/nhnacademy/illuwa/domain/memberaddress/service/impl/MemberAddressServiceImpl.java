package com.nhnacademy.illuwa.domain.memberaddress.service.impl;

import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import com.nhnacademy.illuwa.domain.memberaddress.exception.MemberAddressNotFoundException;
import com.nhnacademy.illuwa.domain.memberaddress.exception.TooManyMemberAddressException;
import com.nhnacademy.illuwa.domain.memberaddress.repo.MemberAddressRepository;
import com.nhnacademy.illuwa.domain.memberaddress.service.MemberAddressService;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.memberaddress.utils.MemberAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberAddressServiceImpl implements MemberAddressService {
    private final MemberRepository memberRepository;
    private final MemberAddressRepository addressRepository;
    private final MemberAddressMapper memberAddressMapper;

    private static final int MAX_ADDRESS_COUNT = 10;

    @Override
    public MemberAddressResponse registerMemberAddress(long memberId, MemberAddressRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException(memberId));

        validateMemberAddressLimit(memberId);
        handleDefaultAddressSetting(memberId, request);

        MemberAddress memberAddress = memberAddressMapper.toEntity(request, member);
        return memberAddressMapper.toDto(addressRepository.save(memberAddress));
    }

    @Override
    public MemberAddressResponse updateMemberAddress(long addressId, MemberAddressRequest request) {
        MemberAddress orgMemberAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new MemberAddressNotFoundException(addressId));

        orgMemberAddress.updateMemberAddress(request);
        return memberAddressMapper.toDto(orgMemberAddress);
    }

    @Override
    public void deleteMemberAddress(long addressId) {
        if(!addressRepository.existsById(addressId)){
            throw new MemberAddressNotFoundException();
        }
        addressRepository.deleteById(addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAddressResponse getMemberAddress(long addressId) {
        MemberAddress memberAddress = addressRepository.findById(addressId)
                .orElseThrow(MemberAddressNotFoundException::new);
        return memberAddressMapper.toDto(memberAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberAddressResponse> getMemberAddressList(long memberId) {
        return addressRepository.findAllByMember_MemberId(memberId)
                .stream()
                .map(memberAddressMapper::toDto)
                .toList();
    }

    @Override
    public int countMemberAddress(long memberId) {
        return addressRepository.countAllByMember_MemberId(memberId);
    }

    private void validateMemberAddressLimit(long memberId) {
        if (addressRepository.countAllByMember_MemberId(memberId) >= MAX_ADDRESS_COUNT) {
            throw new TooManyMemberAddressException();
        }
    }

    private void handleDefaultAddressSetting(long memberId, MemberAddressRequest request) {
        if (Boolean.TRUE.equals(request.isDefaultAddress())) {
            addressRepository.unsetAllDefaultForMember(memberId);
        } else {
            boolean hasDefault = addressRepository.findDefaultMemberAddress(memberId).isPresent();
            if (!hasDefault) {
                request.setDefaultAddress(true);
            }
        }
    }
}