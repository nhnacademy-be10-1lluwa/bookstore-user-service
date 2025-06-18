package com.nhnacademy.illuwa.domain.address.service.impl;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;
import com.nhnacademy.illuwa.domain.address.exception.DuplicateAddressException;
import com.nhnacademy.illuwa.domain.address.exception.AddressNotFoundException;
import com.nhnacademy.illuwa.domain.address.repo.AddressRepository;
import com.nhnacademy.illuwa.domain.address.service.AddressService;
import com.nhnacademy.illuwa.domain.address.utils.AddressMapper;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.exception.GuestNotFoundException;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {
    private final MemberRepository memberRepository;
    private final GuestRepository guestRepository;
    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    @Override
    public AddressResponse registerAddressForMember(long memberId, AddressRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException(memberId));

        Address address = addressMapper.toEntity(request);
        address.setMember(member);

        return addressMapper.addressToDto(addressRepository.save(address));
    }

    @Override
    public AddressResponse registerAddressForGuest(long guestId, AddressRequest request) {
        if(addressRepository.existsByGuest_GuestId(guestId)) {
            throw new DuplicateAddressException("비회원은 주소 1개만 설정할 수 있습니다.");
        }
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));
        Address address = addressMapper.toEntity(request);
        address.setGuest(guest);

        Address savedAddress = addressRepository.save(address);

        return addressMapper.addressToDto(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(long addressId, AddressRequest request) {
        Address orgAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        Address newAddress = addressMapper.toEntity(request);
        newAddress = addressMapper.updateAddress(orgAddress, newAddress);

        return addressMapper.addressToDto(newAddress);
    }

    @Override
    public void deleteAddress(long addressId) {
        if(!addressRepository.existsById(addressId)){
            throw new AddressNotFoundException();
        }
        addressRepository.deleteById(addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddress(long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(AddressNotFoundException::new);
        return addressMapper.addressToDto(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByMember(long memberId) {
        List<AddressResponse> addressDtoList = addressRepository.findAllByMember_MemberId(memberId)
                .stream()
                .map(addressMapper::addressToDto)
                .toList();

        return addressDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressByGuest(long guestId) {
        Address guestAddress =  addressRepository.findAddressByGuest_GuestId(guestId)
                .orElseThrow(() -> new AddressNotFoundException("해당 비회원의 주소가 등록되지 않았습니다."));

        return addressMapper.addressToDto(guestAddress);
    }
}
