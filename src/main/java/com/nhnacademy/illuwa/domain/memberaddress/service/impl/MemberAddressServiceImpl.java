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
    private final MemberAddressRepository memberAddressRepository;

    @Override
    public MemberAddressResponse registerMemberAddress(long memberId, MemberAddressRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException(memberId));

        if(memberAddressRepository.countAllByMember_MemberId(memberId) >= 10){
            throw new TooManyMemberAddressException();
        }
        if(request.getIsDefault()){
            addressRepository.unsetAllDefaultForMember(memberId);
        }
        MemberAddress memberAddress = memberAddressMapper.toEntity(request);
        memberAddress.setMember(member);

        return memberAddressMapper.toDto(addressRepository.save(memberAddress));
    }

    @Override
    public MemberAddressResponse updateMemberAddress(long addressId, MemberAddressRequest request) {
        MemberAddress orgMemberAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new MemberAddressNotFoundException(addressId));

        MemberAddress newMemberAddress = memberAddressMapper.toEntity(request);
        newMemberAddress = memberAddressMapper.updateMemberAddress(orgMemberAddress, newMemberAddress);

        return memberAddressMapper.toDto(newMemberAddress);
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

}
