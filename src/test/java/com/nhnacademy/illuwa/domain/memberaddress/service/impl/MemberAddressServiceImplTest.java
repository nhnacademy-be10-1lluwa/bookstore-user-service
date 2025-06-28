package com.nhnacademy.illuwa.domain.memberaddress.service.impl;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import com.nhnacademy.illuwa.domain.memberaddress.exception.MemberAddressNotFoundException;
import com.nhnacademy.illuwa.domain.memberaddress.exception.TooManyMemberAddressException;
import com.nhnacademy.illuwa.domain.memberaddress.repo.MemberAddressRepository;
import com.nhnacademy.illuwa.domain.memberaddress.utils.MemberAddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class MemberAddressServiceImplTest {

    @Autowired
    MemberAddressMapper memberAddressMapper;

    MemberRepository memberRepository;
    MemberAddressRepository addressRepository;
    MemberAddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        addressRepository = mock(MemberAddressRepository.class);
        addressService = new MemberAddressServiceImpl(memberRepository, addressRepository, memberAddressMapper);
    }

    MemberAddressRequest createRequest(boolean isDefault) {
        return MemberAddressRequest.builder()
                .postCode("12345")
                .address("서울시 강남구")
                .detailAddress("101호")
                .addressName("집")
                .recipientName("공주님")
                .recipientContact("010-1234-5678")
                .defaultAddress(isDefault)
                .build();
    }

    void setAddressId(MemberAddress addr, long id) {
        try {
            Field field = MemberAddress.class.getDeclaredField("memberAddressId");
            field.setAccessible(true);
            field.set(addr, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("회원 주소 등록 - 기본배송지 true")
    void registerAddress_defaultTrue() {
        Member member = new Member();
        MemberAddressRequest request = createRequest(true);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(addressRepository.countAllByMember_MemberId(1L)).thenReturn(0);
        when(addressRepository.save(any(MemberAddress.class))).thenAnswer(i -> i.getArgument(0));

        MemberAddressResponse response = addressService.registerMemberAddress(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getRecipientName()).isEqualTo("공주님");
        assertThat(response.isDefaultAddress()).isTrue();
        verify(addressRepository).unsetAllDefaultForMember(1L);
    }

    @Test
    @DisplayName("회원 주소 등록 - false 체크해도 기본주소 없는 경우 자동 true")
    void registerAddress_forceDefaultIfNoneExists() {
        Member member = new Member();
        MemberAddressRequest request = createRequest(false);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(addressRepository.countAllByMember_MemberId(1L)).thenReturn(0);
        when(addressRepository.findDefaultMemberAddress(1L)).thenReturn(Optional.empty());
        when(addressRepository.save(any(MemberAddress.class))).thenAnswer(i -> i.getArgument(0));

        MemberAddressResponse response = addressService.registerMemberAddress(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.isDefaultAddress()).isTrue();
    }

    @Test
    @DisplayName("회원 주소 등록 - 기본주소 false&기존 기본주소 있음")
    void registerAddress_notDefault_andDefaultAlreadyExists() {
        Member member = new Member();
        MemberAddressRequest request = createRequest(false);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(addressRepository.countAllByMember_MemberId(1L)).thenReturn(0);
        when(addressRepository.findDefaultMemberAddress(1L)).thenReturn(Optional.of(new MemberAddress())); // 기본 주소 존재
        when(addressRepository.save(any(MemberAddress.class))).thenAnswer(i -> i.getArgument(0));

        MemberAddressResponse response = addressService.registerMemberAddress(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.isDefaultAddress()).isFalse();
        verify(addressRepository, never()).unsetAllDefaultForMember(1L);
    }

    @Test
    @DisplayName("회원 주소 등록 실패 - 회원 없음")
    void registerAddress_memberNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.registerMemberAddress(1L, createRequest(true)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 등록 실패 - 최대 10개 초과")
    void registerAddress_tooMany() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
        when(addressRepository.countAllByMember_MemberId(1L)).thenReturn(10);

        assertThatThrownBy(() -> addressService.registerMemberAddress(1L, createRequest(true)))
                .isInstanceOf(TooManyMemberAddressException.class);
    }

    @Test
    @DisplayName("회원 주소 수정 성공")
    void updateAddress_success() {
        MemberAddress old = memberAddressMapper.toEntity(createRequest(true), new Member());
        setAddressId(old, 3L);

        MemberAddressRequest updateReq = createRequest(false);
        updateReq.setPostCode("99999");
        updateReq.setRecipientName("테스트공주");

        when(addressRepository.findById(3L)).thenReturn(Optional.of(old));

        MemberAddressResponse response = addressService.updateMemberAddress(3L, updateReq);

        assertThat(response.getPostCode()).isEqualTo("99999");
        assertThat(response.getRecipientName()).isEqualTo("테스트공주");
    }

    @Test
    @DisplayName("회원 주소 수정 실패 - 주소 없음")
    void updateAddress_notFound() {
        when(addressRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.updateMemberAddress(3L, createRequest(true)))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 삭제 성공")
    void deleteAddress_success() {
        when(addressRepository.existsById(5L)).thenReturn(true);

        addressService.deleteMemberAddress(5L);

        verify(addressRepository).deleteById(5L);
    }

    @Test
    @DisplayName("회원 주소 삭제 실패 - 주소 없음")
    void deleteAddress_notFound() {
        when(addressRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> addressService.deleteMemberAddress(5L))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 단건 조회 성공")
    void getAddress_success() {
        MemberAddress addr = memberAddressMapper.toEntity(createRequest(true), new Member());
        setAddressId(addr, 10L);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(addr));

        MemberAddressResponse response = addressService.getMemberAddress(10L);

        assertThat(response.getMemberAddressId()).isEqualTo(10L);
        assertThat(response.getRecipientName()).isEqualTo("공주님");
    }

    @Test
    @DisplayName("회원 주소 단건 조회 실패")
    void getAddress_notFound() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getMemberAddress(10L))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 목록 조회")
    void getAddressList() {
        MemberAddress addr = memberAddressMapper.toEntity(createRequest(true), new Member());
        setAddressId(addr, 7L);
        when(addressRepository.findAllByMember_MemberId(1L)).thenReturn(List.of(addr));

        List<MemberAddressResponse> list = addressService.getMemberAddressList(1L);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getMemberAddressId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("회원 주소 목록 조회 - 빈 목록")
    void getAddressList_empty() {
        MemberAddress addr = memberAddressMapper.toEntity(createRequest(true), new Member());
        setAddressId(addr, 7L);
        when(addressRepository.findAllByMember_MemberId(1L)).thenReturn(Optional.);

        List<MemberAddressResponse> list = addressService.getMemberAddressList(1L);

        assertThat(list).hasSize(0);
    }
}
