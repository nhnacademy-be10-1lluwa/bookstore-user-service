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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Slf4j
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
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        validateMemberAddressLimit(memberId);

        MemberAddress memberAddress = memberAddressMapper.toEntity(request, member);
        MemberAddress savedAddress = addressRepository.save(memberAddress);

        updateDefaultAddressIfNeeded(memberId, savedAddress.getMemberAddressId(), request.isDefaultAddress());

        // TODO flush 사용 지양, 영속성 컨텍스트 사이 동기화 문제 해결
        // 객체 상태를 변경하려면 리팩토링 필요
//        addressRepository.flush();
        return memberAddressMapper.toDto(savedAddress);
    }

    @Override
    public MemberAddressResponse updateMemberAddress(long memberId, long addressId, MemberAddressRequest request) {
        MemberAddress orgMemberAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new MemberAddressNotFoundException(addressId));
        orgMemberAddress.updateMemberAddress(request);

        updateDefaultAddressIfNeeded(memberId, addressId, request.isDefaultAddress());

        // TODO flush 사용 지양, 영속성 컨텍스트 사이 동기화 문제 해결
//        addressRepository.flush();
        return memberAddressMapper.toDto(orgMemberAddress);
    }

    @Override
    public void deleteMemberAddress(long memberId, long addressId) {
        if(!memberRepository.existsById(memberId)){
            throw new MemberNotFoundException(memberId);
        }
        MemberAddress address = addressRepository.findById(addressId)
                .orElseThrow(MemberAddressNotFoundException::new);
        boolean wasDefault = address.isDefaultAddress();
        addressRepository.delete(address);

        if (wasDefault) {
            List<MemberAddress> remains = addressRepository.findAllByMember_MemberIdOrderByCreatedAtAsc(memberId);
            if (!remains.isEmpty()) {
                remains.getFirst().changeDefaultAddress(true);
            }
        }
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
    @Transactional(readOnly = true)
    public Page<MemberAddressResponse> getPagedMemberAddressList(long memberId, Pageable pageable) {
        Page<MemberAddress> page = addressRepository.findAllByMember_MemberId(memberId, pageable);
        return page.map(memberAddressMapper::toDto);
    }

    @Override
    public int countMemberAddress(long memberId) {
        return addressRepository.countAllByMember_MemberId(memberId);
    }

    @Override
    public void setDefaultAddress(long memberId, long addressId) {
        MemberAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new MemberAddressNotFoundException(addressId));
        if (!address.isDefaultAddress()) {
            addressRepository.unsetAllDefaultForMember(memberId);
            addressRepository.setDefaultAddress(memberId, addressId);
        }
    }

    private void validateMemberAddressLimit(long memberId) {
        if (addressRepository.countAllByMember_MemberId(memberId) >= MAX_ADDRESS_COUNT) {
            throw new TooManyMemberAddressException();
        }
    }

    private void updateDefaultAddressIfNeeded(long memberId, long addressId, Boolean isDefaultRequested) {
        if (Boolean.TRUE.equals(isDefaultRequested)) {
            addressRepository.unsetAllDefaultForMember(memberId);
            addressRepository.setDefaultAddress(memberId, addressId);
        } else {
            boolean hasDefault = addressRepository.findDefaultMemberAddress(memberId).isPresent();
            if (!hasDefault) {
                addressRepository.setDefaultAddress(memberId, addressId);
            }
        }
    }
}