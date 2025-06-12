package com.nhnacademy.illuwa.domain.address.repo;

import com.nhnacademy.illuwa.domain.address.entity.Address;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    GuestRepository guestRepository;

    @Test
    @DisplayName("Address 저장 테스트 - Member")
    void saveAddress_withMember_shouldSucceed() {
        Member member = Member.builder()
                .name("카리나")
                .build();
        memberRepository.save(member);

        Address address = Address.builder()
                .addressName("내 집")
                .recipient("유지민")
                .contact("010-1234-5678")
                .addressDetail("서울시 강남구 테헤란로 123")
                .isDefault(true)
                .member(member)
                .build();

        Address saved = addressRepository.save(address);

        assertThat(saved.getAddressId()).isPositive();
        assertThat(saved.getAddressDetail()).contains("로");
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.isDefault()).isTrue();
        assertThat(saved.getGuest()).isNull();
    }

    @Test
    @DisplayName("Address 저장 테스트 - Guest")
    void saveAddress_withGuest_shouldSucceed() {
        Guest guest = Guest.builder()
                .name("비회원")
                .build();
        guestRepository.save(guest);

        Address address = Address.builder()
                .addressName("임시 배송지")
                .recipient("홍길동")
                .contact("010-9999-9999")
                .addressDetail("서울시 성동구 왕십리로 88")
                .isDefault(false)
                .guest(guest)
                .build();

        Address saved = addressRepository.save(address);

        assertThat(saved.getAddressId()).isPositive();
        assertThat(saved.getAddressDetail()).contains("로");
        assertThat(saved.getGuest()).isEqualTo(guest);
        assertThat(saved.isDefault()).isFalse();
        assertThat(saved.getMember()).isNull();
    }
}
