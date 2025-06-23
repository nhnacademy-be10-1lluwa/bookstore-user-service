package com.nhnacademy.illuwa.domain.address.service.impl;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;
import com.nhnacademy.illuwa.domain.address.exception.AddressNotFoundException;
import com.nhnacademy.illuwa.domain.address.exception.DuplicateAddressException;
import com.nhnacademy.illuwa.domain.address.repo.AddressRepository;
import com.nhnacademy.illuwa.domain.address.utils.AddressMapperImpl;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.exception.GuestNotFoundException;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class AddressServiceImplTest {
    @Mock
    MemberRepository memberRepository;

    @Mock
    GuestRepository guestRepository;

    @Mock
    AddressRepository addressRepository;

    AddressMapperImpl addressMapper = new AddressMapperImpl();

    @InjectMocks
    AddressServiceImpl addressService;

    AddressRequest request;

    void setAddressId(Address address, long addressId) {
        try {
            Field field = Address.class.getDeclaredField("addressId");
            field.setAccessible(true);
            field.set(address, addressId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressService = new AddressServiceImpl(memberRepository, guestRepository, addressRepository, addressMapper);

        request = new AddressRequest("집", "카리나", "010-1234-5678", "서울 강남구", true);
    }

    @Test
    @DisplayName("registerAddressForMember - 기본배송지 있을 때 정상 등록")
    void testRegisterAddressForMember_defaultTrue() {
        Member member = new Member();
        Address oldAddress = new Address();
        oldAddress.setDefault(true);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(addressRepository.findMemberDefaultAddress(anyLong())).thenReturn(Optional.of(oldAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        AddressRequest request = new AddressRequest("집", "수령인", "010-1234-5678", "주소", true);

        AddressResponse response = addressService.registerAddressForMember(1L, request);

        assertThat(response).isNotNull();
        verify(addressRepository).findMemberDefaultAddress(anyLong());
        verify(addressRepository).save(any(Address.class));
        verify(addressMapper).addressToDto(addressRepository.save(any(Address.class)));
        assertThat(oldAddress.isDefault()).isFalse();
    }

    @Test
    @DisplayName("registerAddressForMember - 기본배송지 없을 때 예외 발생")
    void testRegisterAddressForMember_defaultTrue_noOldAddress() {
        Member member = new Member();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(addressRepository.findMemberDefaultAddress(anyLong())).thenReturn(Optional.empty());

        AddressRequest request = new AddressRequest("집", "수령인", "010-1234-5678", "주소", true);

        assertThatThrownBy(() -> addressService.registerAddressForMember(1L, request))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    @DisplayName("registerAddressForMember - 기본배송지 false 일 때 등록")
    void testRegisterAddressForMember_defaultFalse() {
        Member member = new Member();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        AddressRequest request = new AddressRequest("집", "수령인", "010-1234-5678", "주소", false);

        AddressResponse response = addressService.registerAddressForMember(1L, request);

        assertThat(response).isNotNull();
        verify(addressRepository, never()).findMemberDefaultAddress(anyLong());
        verify(addressRepository).save(any(Address.class));
    }



    @Test
    @DisplayName("회원 주소 등록 - 회원 없음")
    void registerAddressForMember_memberNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.registerAddressForMember(1L, request))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    @DisplayName("비회원 주소 등록 성공")
    void registerAddressForGuest_success() {
        Guest guest = new Guest();
        when(addressRepository.existsByGuest_GuestId(2L)).thenReturn(false);
        when(guestRepository.findById(2L)).thenReturn(Optional.of(guest));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.registerAddressForGuest(2L, request);

        assertThat(response.getRecipient()).isEqualTo("카리나");
        assertThat(response.getAddressDetail()).isEqualTo("서울 강남구");
        assertThat(response.getContact()).isEqualTo("010-1234-5678");
        assertThat(response.getAddressName()).isEqualTo("집");
        assertThat(response.isDefault()).isTrue();
    }

    @Test
    @DisplayName("비회원 주소 등록 중복")
    void registerAddressForGuest_duplicate() {
        when(addressRepository.existsByGuest_GuestId(2L)).thenReturn(true);

        assertThatThrownBy(() -> addressService.registerAddressForGuest(2L, request))
                .isInstanceOf(DuplicateAddressException.class)
                .hasMessageContaining("비회원은 주소 1개만");
    }

    @Test
    @DisplayName("비회원 주소 등록 - 게스트 없음")
    void registerAddressForGuest_guestNotFound() {
        when(addressRepository.existsByGuest_GuestId(2L)).thenReturn(false);
        when(guestRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.registerAddressForGuest(2L, request))
                .isInstanceOf(GuestNotFoundException.class)
                .hasMessageContaining("2");
    }

    @Test
    @DisplayName("주소 수정")
    void updateAddress_success() {
        Address old = addressMapper.toEntity(request);
        setAddressId(old, 3L);
        when(addressRepository.findById(3L)).thenReturn(Optional.of(old));

        AddressResponse result = addressService.updateAddress(3L, request);

        assertThat(result.getAddressName()).isEqualTo("집");
        assertThat(result.getRecipient()).isEqualTo("카리나");
        assertThat(result.getContact()).isEqualTo("010-1234-5678");
        assertThat(result.getAddressDetail()).isEqualTo("서울 강남구");
        assertThat(result.isDefault()).isTrue();
    }

    @Test
    @DisplayName("주소 수정 실패")
    void updateAddress_notFound() {
        when(addressRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.updateAddress(3L, request))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("3");
    }

    @Test
    @DisplayName("주소 삭제 성공")
    void deleteAddress_success() {
        when(addressRepository.existsById(4L)).thenReturn(true);

        addressService.deleteAddress(4L);

        verify(addressRepository).deleteById(4L);
    }

    @Test
    @DisplayName("주소 삭제 실패")
    void deleteAddress_notFound() {
        when(addressRepository.existsById(4L)).thenReturn(false);

        assertThatThrownBy(() -> addressService.deleteAddress(4L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    @DisplayName("주소 단건 조회")
    void getAddress_success() {
        Address address = addressMapper.toEntity(request);
        setAddressId(address, 5L);
        when(addressRepository.findById(5L)).thenReturn(Optional.of(address));

        AddressResponse response = addressService.getAddress(5L);

        assertThat(response.getAddressId()).isEqualTo(5L);
        assertThat(response.getRecipient()).isEqualTo("카리나");
        assertThat(response.getContact()).isEqualTo("010-1234-5678");
        assertThat(response.getAddressDetail()).isEqualTo("서울 강남구");
    }

    @Test
    @DisplayName("주소 단건 조회 실패")
    void getAddress_notFound() {
        when(addressRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getAddress(5L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    @DisplayName("회원 주소 목록 조회")
    void getAddressesByMember() {
        Address addr = addressMapper.toEntity(request);
        when(addressRepository.findAllByMember_MemberId(1L)).thenReturn(List.of(addr));

        List<AddressResponse> result = addressService.getAddressesByMember(1L);

        assertThat(result).hasSize(1);
        AddressResponse res = result.getFirst();
        assertThat(res.getRecipient()).isEqualTo("카리나");
        assertThat(res.getAddressDetail()).isEqualTo("서울 강남구");
        assertThat(res.isDefault()).isTrue();
    }

    @Test
    @DisplayName("비회원 주소 조회 성공")
    void getAddressByGuest_success() {
        Address address = addressMapper.toEntity(request);
        when(addressRepository.findAddressByGuest_GuestId(2L)).thenReturn(Optional.of(address));

        AddressResponse response = addressService.getAddressByGuest(2L);

        assertThat(response.getRecipient()).isEqualTo("카리나");
        assertThat(response.getAddressDetail()).isEqualTo("서울 강남구");
    }

    @Test
    @DisplayName("비회원 주소 조회 실패")
    void getAddressByGuest_notFound() {
        when(addressRepository.findAddressByGuest_GuestId(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getAddressByGuest(2L))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("등록되지 않았습니다");
    }

    @Test
    @DisplayName("AddressRequest DTO setter/getter 커버리지")
    void addressRequestDtoSetterGetterTest() {
        AddressRequest req = new AddressRequest();

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
        AddressResponse res = new AddressResponse();

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
