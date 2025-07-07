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
import com.nhnacademy.illuwa.domain.memberaddress.utils.MemberAddressMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberAddressServiceImplTest {

    MemberAddressMapper memberAddressMapper = new MemberAddressMapperImpl();

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAddressRepository addressRepository;

    @InjectMocks
    MemberAddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        addressService = new MemberAddressServiceImpl(
                memberRepository, addressRepository, memberAddressMapper
        );
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
    @Disabled
    @DisplayName("회원 주소 등록 - false 체크해도 기본주소 없는 경우 자동 true")
    void registerAddress_forceDefaultIfNoneExists() {
        Member member = new Member();
        MemberAddressRequest request = createRequest(false);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(addressRepository.countAllByMember_MemberId(1L)).thenReturn(0);
        when(addressRepository.findDefaultMemberAddress(1L)).thenReturn(Optional.empty());

        when(addressRepository.save(any(MemberAddress.class))).thenAnswer(invocation -> {
            MemberAddress addr = invocation.getArgument(0);
            setAddressId(addr,100L);
            return addr;
        });

        MemberAddressResponse response = addressService.registerMemberAddress(1L, request);

        verify(addressRepository).setDefaultAddress(1L, 100L);
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
        Member member = new Member();
        MemberAddress old = memberAddressMapper.toEntity(createRequest(true), member);
        setAddressId(old, 3L);

        MemberAddressRequest updateReq = createRequest(false);
        updateReq.setPostCode("99999");
        updateReq.setRecipientName("테스트공주");

        when(addressRepository.findById(3L)).thenReturn(Optional.of(old));

        MemberAddressResponse response = addressService.updateMemberAddress(1L, 3L, updateReq);

        assertThat(response.getPostCode()).isEqualTo("99999");
        assertThat(response.getRecipientName()).isEqualTo("테스트공주");
    }

    @Test
    @DisplayName("회원 주소 수정 실패 - 주소 없음")
    void updateAddress_notFound() {
        when(addressRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.updateMemberAddress(1L, 3L, createRequest(true)))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 삭제 성공 - 기본주소지, 남은 주소 있는 경우")
    void deleteAddress_defaultAddressDeleted_shouldAssignNewDefault() {
        long memberId = 1L;
        long addressId = 100L;

        MemberAddress address = mock(MemberAddress.class);
        when(address.isDefaultAddress()).thenReturn(true);

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        MemberAddress another = mock(MemberAddress.class);
        List<MemberAddress> remaining = List.of(another);
        when(addressRepository.findAllByMember_MemberIdOrderByCreatedAtAsc(memberId)).thenReturn(remaining);

        addressService.deleteMemberAddress(memberId, addressId);

        verify(addressRepository).delete(address);
        verify(addressRepository).findAllByMember_MemberIdOrderByCreatedAtAsc(memberId);
        verify(another).changeDefaultAddress(true);
    }

    @Test
    @DisplayName("회원 주소 삭제 성공 - 기본주소지, 남은 주소 없는 경우")
    void deleteAddress_defaultAddressDeleted_butNoRemainingAddress() {
        long memberId = 1L;
        long addressId = 100L;

        MemberAddress address = mock(MemberAddress.class);
        when(address.isDefaultAddress()).thenReturn(true);
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.findAllByMember_MemberIdOrderByCreatedAtAsc(memberId)).thenReturn(List.of());

        addressService.deleteMemberAddress(memberId, addressId);

        verify(addressRepository).delete(address);
        verify(addressRepository).findAllByMember_MemberIdOrderByCreatedAtAsc(memberId);
    }

    @Test
    @DisplayName("회원 주소 삭제 성공 - 기본주소지 아니었던 경우")
    void deleteAddress_nonDefaultAddressDeleted_shouldNotAssignNewDefault() {
        long memberId = 1L;
        long addressId = 100L;

        MemberAddress address = mock(MemberAddress.class);
        when(address.isDefaultAddress()).thenReturn(false);
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        addressService.deleteMemberAddress(memberId, addressId);

        verify(addressRepository).delete(address);
        verify(addressRepository, never()).findAllByMember_MemberIdOrderByCreatedAtAsc(anyLong());
    }

    @Test
    @DisplayName("회원 주소 삭제 실패 - 회원 없음")
    void deleteAddress_memberNotFound_shouldThrowException() {
        long memberId = 1L;
        long addressId = 100L;
        when(memberRepository.existsById(memberId)).thenReturn(false);

        assertThrows(MemberNotFoundException.class,
                () -> addressService.deleteMemberAddress(memberId, addressId));
    }

    @Test
    @DisplayName("회원 주소 삭제 실패 - 주소 없음")
    void deleteAddress_notFound() {
        long memberId = 1L;
        long addressId = 100L;
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.deleteMemberAddress(memberId, addressId))
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
        assertThat(list.get(0).getMemberAddressId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("회원 주소 목록 조회 - 빈 목록")
    void getAddressList_empty() {
        when(addressRepository.findAllByMember_MemberId(1L)).thenReturn(Collections.emptyList());

        List<MemberAddressResponse> list = addressService.getMemberAddressList(1L);

        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("회원 주소 카운트")
    void countMemberAddress_shouldReturnCorrectCount() {
        long memberId = 1L;
        int expectedCount = 3;
        when(addressRepository.countAllByMember_MemberId(memberId)).thenReturn(expectedCount);

        int actualCount = addressService.countMemberAddress(memberId);

        assertEquals(expectedCount, actualCount);
        verify(addressRepository).countAllByMember_MemberId(memberId);
    }
}
