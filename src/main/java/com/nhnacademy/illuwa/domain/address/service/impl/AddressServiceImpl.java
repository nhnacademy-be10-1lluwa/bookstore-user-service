package com.nhnacademy.illuwa.domain.address.service.impl;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;
import com.nhnacademy.illuwa.domain.address.exception.AddressAlreadyExistsException;
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
public class AddressServiceImpl implements AddressService {
    private final MemberRepository memberRepository;
    private final GuestRepository guestRepository;
    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public Address registerAddressForMember(long memberId, AddressRequest request) {
        if(!memberRepository.existsById(memberId)){
            throw new MemberNotFoundException(memberId);
        }
        Address address = addressMapper.toEntity(request);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException(memberId));
        address.setMember(member);

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address registerAddressForGuest(long guestId, AddressRequest request) {
        //비회원 주소 이미 존재하는지 체크
        if(addressRepository.existsByGuest_GuestId(guestId)) {
            throw new AddressAlreadyExistsException();
        }
        Address address = addressMapper.toEntity(request);

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));
        address.setGuest(guest);

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(long addressId, AddressRequest request) {
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        Address newAddress = addressMapper.toEntity(request);
        Address orgAddress;
        if(optionalAddress.isEmpty()){
            throw new AddressNotFoundException();
        } else{
            orgAddress = optionalAddress.get();
            addressMapper.updateAddress(orgAddress, newAddress);
        }
        return orgAddress;
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
        Address address = addressRepository.findById(addressId).orElseThrow(AddressNotFoundException::new);
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
        Optional<Address> optionalAddress = addressRepository.findAddressByGuest_GuestId(guestId);
        if(optionalAddress.isEmpty()){
            throw new AddressNotFoundException();
        }

        return addressMapper.addressToDto(optionalAddress.get());
    }
}
