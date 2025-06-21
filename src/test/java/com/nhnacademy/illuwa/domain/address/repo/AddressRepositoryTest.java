package com.nhnacademy.illuwa.domain.address.repo;

import com.nhnacademy.illuwa.domain.address.entity.Address;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class AddressRepositoryTest {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    GuestRepository guestRepository;

    Member testMember;
    Guest testGuest;

    @BeforeEach
    public void setUp(){
        Grade grade = gradeRepository.findByGradeName(GradeName.BASIC).orElseThrow();
        Member member = Member.builder()
                .name("카리나")
                .birth("2000-04-11")
                .email("karina@test.com")
                .password("123456!")
                .grade(grade)
                .role(Role.USER)
                .contact("010-1234-5678")
                .build();

        testMember = memberRepository.save(member);

        Guest guest = Guest.builder()
                .name("비회원")
                .email("guest@test.com")
                .orderPassword("123456!")
                .contact("010-9876-5432")
                .orderNumber("20250819063812-234877")
                .build();
        testGuest = guestRepository.save(guest);
    }

    @Test
//    @Commit   // db 들어간 거 확인하려면 주석해제
    @DisplayName("Address 저장 - Member")
    void testSaveMemberAddress() {
        Address address = Address.builder()
                .addressName("우리집")
                .recipient("유지민")
                .contact("010-1234-5678")
                .addressDetail("서울시 강남구 테헤란로 123")
                .isDefault(true)
                .member(testMember)
                .build();

        Address savedAddress = addressRepository.save(address);

        assertThat(savedAddress.getAddressId()).isPositive();
        assertThat(savedAddress.getAddressDetail()).contains("로");
        assertThat(savedAddress.getMember()).isEqualTo(testMember);
        assertThat(savedAddress.isDefault()).isTrue();
        assertThat(savedAddress.getGuest()).isNull();
    }

    @Test
//    @Commit
    @DisplayName("Address 저장 - Guest")
    void testSaveGuestAddress() {
        Address address = Address.builder()
                .addressName("임시 배송지")
                .recipient("홍길동")
                .contact("010-9999-9999")
                .addressDetail("서울시 성동구 왕십리로 88")
                .isDefault(false)
                .guest(testGuest)
                .build();

        Address saved = addressRepository.save(address);

        assertThat(saved.getAddressId()).isPositive();
        assertThat(saved.getAddressDetail()).contains("로");
        assertThat(saved.getGuest()).isEqualTo(testGuest);
        assertThat(saved.isDefault()).isFalse();
        assertThat(saved.getMember()).isNull();
    }

    @Test
    @Commit
    @DisplayName("Member의 Address 수정")
    void testUpdateAddress(){
        Address address = Address.builder()
                .addressName("선배님 댁")
                .recipient("장도연")
                .contact("010-2222-2222")
                .addressDetail("광주광역시 동구 필문대로 123")
                .isDefault(false)
                .member(testMember)
                .build();

        Address saved = addressRepository.save(address);
        saved.setAddressName("도연언니 댁");
        saved.setAddressDetail("경기도 고양시 일산동구 월드고양로 102-65");

        Address updated = addressRepository.save(saved);

        assertEquals(saved.getAddressId(), updated.getAddressId());
        assertEquals("도연언니 댁", updated.getAddressName());
        assertEquals("장도연", updated.getRecipient());
        assertEquals("경기도 고양시 일산동구 월드고양로 102-65", updated.getAddressDetail());

    }

    @Test
    @Commit
    @DisplayName("Member의 Address 삭제")
    void testDeleteAddress() {
        Address address = Address.builder()
                .addressName("삭제할 집")
                .recipient("김도영")
                .contact("010-0000-0000")
                .addressDetail("서울시 강남구 삭제로 1")
                .isDefault(false)
                .member(testMember)
                .build();

        Address saved = addressRepository.save(address);
        Long id = saved.getAddressId();

        addressRepository.deleteById(id);

        Optional<Address> deleted = addressRepository.findById(id);
        assertTrue(deleted.isEmpty(), "삭제된 주소는 조회되지 않아야 합니다.");
    }

    @Test
    @Commit
    @DisplayName("Member의 기본 배송지 조회")
    void testFindDefaultAddressByMember() {
        Address defaultAddress = Address.builder()
                .addressName("기본 집")
                .recipient("기본 수령인")
                .contact("010-1111-2222")
                .addressDetail("서울시 용산구 기본로 123")
                .isDefault(true)
                .member(testMember)
                .build();

        addressRepository.save(defaultAddress);

        Optional<Address> found = addressRepository.findMemberDefaultAddress(testMember.getMemberId());

        assertTrue(found.isPresent(), "기본 배송지가 조회되어야 합니다.");
        assertTrue(found.get().isDefault(), "조회된 주소는 기본 배송지여야 합니다.");
        assertEquals(testMember, found.get().getMember());
    }

    @Test
    @Commit
    @DisplayName("Member의 모든 주소 조회")
    void testFindAllByMember() {
        Address addr1 = Address.builder()
                .addressName("집1")
                .recipient("유지민")
                .contact("010-1111-1111")
                .addressDetail("서울시 강남구 1번지")
                .isDefault(false)
                .member(testMember)
                .build();

        Address addr2 = Address.builder()
                .addressName("집2")
                .recipient("유지민")
                .contact("010-2222-2222")
                .addressDetail("서울시 강남구 2번지")
                .isDefault(false)
                .member(testMember)
                .build();

        addressRepository.save(addr1);
        addressRepository.save(addr2);

        List<Address> addresses = addressRepository.findAllByMember_MemberId(testMember.getMemberId())
                .stream()
                .toList();

        assertThat(addresses).isNotEmpty();
        assertThat(addresses.size()).isGreaterThanOrEqualTo(2);
        addresses.forEach(address -> assertEquals(testMember, address.getMember()));
    }

}
