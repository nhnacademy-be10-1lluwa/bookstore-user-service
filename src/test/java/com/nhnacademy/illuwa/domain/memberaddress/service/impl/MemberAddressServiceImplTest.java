package com.nhnacademy.illuwa.domain.memberaddress.service.impl;

import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import com.nhnacademy.illuwa.domain.memberaddress.exception.MemberAddressNotFoundException;
import com.nhnacademy.illuwa.domain.memberaddress.repo.MemberAddressRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.memberaddress.utils.MemberAddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
@Disabled
public class MemberAddressServiceImplTest {
    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAddressRepository memberAddressRepository;

    @Autowired
    MemberAddressMapper memberAddressMapper;

    @InjectMocks
    MemberAddressServiceImpl addressService;

    MemberAddressRequest request;

    void setAddressId(MemberAddress memberAddress, long memberAddressId) {
        try {
            Field field = MemberAddress.class.getDeclaredField("memberAddressId");
            field.setAccessible(true);
            field.set(memberAddress, memberAddressId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressService = new MemberAddressServiceImpl(memberRepository,memberAddressRepository, memberAddressMapper);

        request = new MemberAddressRequest("집", "카리나", "010-1234-5678", "서울 강남구", true);
    }

    @Test
    @DisplayName("registerAddressForMember - 기본배송지 있을 때 정상 등록")
    void testRegisterAddressForMember_defaultTrue() {
        Member member = new Member();
        MemberAddress oldMemberAddress = new MemberAddress();
        oldMemberAddress.setDefault(true);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberAddressRepository.findDefaultMemberAddress(anyLong())).thenReturn(Optional.of(oldMemberAddress));
        when(memberAddressRepository.save(any(MemberAddress.class))).thenAnswer(i -> i.getArgument(0));

        MemberAddressRequest request = new MemberAddressRequest("집", "수령인", "010-1234-5678", "주소", true);

        MemberAddressResponse response = addressService.registerMemberAddress(1L, request);

        assertThat(response).isNotNull();
        verify(memberAddressRepository).findDefaultMemberAddress(anyLong());
        verify(memberAddressRepository).save(any(MemberAddress.class));
        assertThat(oldMemberAddress.isDefault()).isFalse();

        assertThat(response.getRecipient()).isEqualTo(request.getRecipient());
        assertThat(response.getAddressName()).isEqualTo(request.getAddressName());
        assertThat(response.getContact()).isEqualTo(request.getContact());
        assertThat(response.getAddressDetail()).isEqualTo(request.getAddressDetail());
    }

    @Test
    @DisplayName("registerAddressForMember - 기본배송지 없을 때 예외 발생")
    void testRegisterAddressForMember_defaultTrue_noOldAddress() {
        Member member = new Member();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberAddressRepository.findDefaultMemberAddress(anyLong())).thenReturn(Optional.empty());

        MemberAddressRequest request = new MemberAddressRequest("집", "수령인", "010-1234-5678", "주소", true);

        assertThatThrownBy(() -> addressService.registerMemberAddress(1L, request))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("registerAddressForMember - 기본배송지 false 일 때 등록")
    void testRegisterAddressForMember_defaultFalse() {
        Member member = new Member();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberAddressRepository.save(any(MemberAddress.class))).thenAnswer(i -> i.getArgument(0));

        MemberAddressRequest request = new MemberAddressRequest("집", "수령인", "010-1234-5678", "주소", false);

        MemberAddressResponse response = addressService.registerMemberAddress(1L, request);

        assertThat(response).isNotNull();
        verify(memberAddressRepository, never()).findDefaultMemberAddress(anyLong());
        verify(memberAddressRepository).save(any(MemberAddress.class));
    }



    @Test
    @DisplayName("회원 주소 등록 - 회원 없음")
    void registerAddressForMember_memberNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.registerMemberAddress(1L, request))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    @DisplayName("주소 수정")
    void updateAddress_success() {
        MemberAddress old = memberAddressMapper.toEntity(request);
        setAddressId(old, 3L);
        when(memberAddressRepository.findById(3L)).thenReturn(Optional.of(old));

        MemberAddressResponse result = addressService.updateMemberAddress(3L, request);

        assertThat(result.getAddressName()).isEqualTo("집");
        assertThat(result.getRecipient()).isEqualTo("카리나");
        assertThat(result.getContact()).isEqualTo("010-1234-5678");
        assertThat(result.getAddressDetail()).isEqualTo("서울 강남구");
        assertThat(result.isDefault()).isTrue();
    }

    @Test
    @DisplayName("주소 수정 실패")
    void updateAddress_notFound() {
        when(memberAddressRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.updateMemberAddress(3L, request))
                .isInstanceOf(MemberAddressNotFoundException.class)
                .hasMessageContaining("3");
    }

    @Test
    @DisplayName("주소 삭제 성공")
    void deleteAddress_success() {
        when(memberAddressRepository.existsById(4L)).thenReturn(true);

        addressService.deleteMemberAddress(4L);

        verify(memberAddressRepository).deleteById(4L);
    }

    @Test
    @DisplayName("주소 삭제 실패")
    void deleteAddress_notFound() {
        when(memberAddressRepository.existsById(4L)).thenReturn(false);

        assertThatThrownBy(() -> addressService.deleteMemberAddress(4L))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("주소 단건 조회")
    void getAddress_success() {
        MemberAddress memberAddress = memberAddressMapper.toEntity(request);
        setAddressId(memberAddress, 5L);
        when(memberAddressRepository.findById(5L)).thenReturn(Optional.of(memberAddress));

        MemberAddressResponse response = addressService.getMemberAddress(5L);

        assertThat(response.getAddressId()).isEqualTo(5L);
        assertThat(response.getRecipient()).isEqualTo("카리나");
        assertThat(response.getContact()).isEqualTo("010-1234-5678");
        assertThat(response.getAddressDetail()).isEqualTo("서울 강남구");
    }

    @Test
    @DisplayName("주소 단건 조회 실패")
    void getAddress_notFound() {
        when(memberAddressRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getMemberAddress(5L))
                .isInstanceOf(MemberAddressNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 목록 조회")
    void getAddressesByMember() {
        MemberAddress addr = memberAddressMapper.toEntity(request);
        when(memberAddressRepository.findAllByMember_MemberId(1L)).thenReturn(List.of(addr));

        List<MemberAddressResponse> result = addressService.getMemberAddressList(1L);

        assertThat(result).hasSize(1);
        MemberAddressResponse res = result.getFirst();
        assertThat(res.getRecipient()).isEqualTo("카리나");
        assertThat(res.getAddressDetail()).isEqualTo("서울 강남구");
        assertThat(res.isDefault()).isTrue();
    }

    @Test
    @DisplayName("AddressRequest DTO setter/getter 커버리지")
    void addressRequestDtoSetterGetterTest() {
        MemberAddressRequest req = new MemberAddressRequest();

        req.setAddressName("회사");
        req.setRecipient("아이린");
        req.setContact("010-9999-8888");
        req.setAddressDetail("서울 서초구");
        req.setIsDefault(false);

        assertThat(req.getAddressName()).isEqualTo("회사");
        assertThat(req.getRecipient()).isEqualTo("아이린");
        assertThat(req.getContact()).isEqualTo("010-9999-8888");
        assertThat(req.getAddressDetail()).isEqualTo("서울 서초구");
        assertThat(req.getIsDefault()).isFalse();
    }

    @Test
    @DisplayName("AddressResponse DTO setter/getter 커버리지")
    void addressResponseDtoSetterGetterTest() {
        MemberAddressResponse res = new MemberAddressResponse();

        res.setAddressId(100L);
        res.setAddressName("회사");
        res.setRecipient("아이린");
        res.setContact("010-9999-8888");
        res.setAddressDetail("서울 서초구");
        res.setDefault(true);

        assertThat(res.getAddressId()).isEqualTo(100L);
        assertThat(res.getAddressName()).isEqualTo("회사");
        assertThat(res.getRecipient()).isEqualTo("아이린");
        assertThat(res.getContact()).isEqualTo("010-9999-8888");
        assertThat(res.getAddressDetail()).isEqualTo("서울 서초구");
        assertThat(res.isDefault()).isTrue();
    }

}
