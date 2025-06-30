package com.nhnacademy.illuwa.domain.memberaddress.repo;

import com.nhnacademy.illuwa.common.testconfig.GradeTestDataConfig;
import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({MemberAddressRepositoryImpl.class, GradeTestDataConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberAddressRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @TestConfiguration
    static class QueryDslTestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Autowired
    GradeTestDataConfig gradeData;

    @Autowired
    MemberAddressRepository memberAddressRepository;

    @Autowired
    MemberRepository memberRepository;

    Member testMember;

    @BeforeEach
    public void setUp() {
        Grade basicGrade = gradeData.getBasicGrade();

        Member member = Member.builder()
                .name("카리나")
                .birth("2000-04-11")
                .email("karina@test.com")
                .password("123456!")
                .grade(basicGrade)
                .role(Role.USER)
                .contact("010-1234-5678")
                .build();

        testMember = memberRepository.save(member);
    }

    @Test
    @DisplayName("MemberAddress 저장")
    void testSaveMemberAddress() {
        MemberAddress memberAddress = MemberAddress.builder()
                .addressName("우리집")
                .recipientName("유지민")
                .recipientContact("010-1234-5678")
                .address("서울시 강남구 테헤란로")
                .detailAddress("123")
                .defaultAddress(true)
                .member(testMember)
                .build();

        MemberAddress saved = memberAddressRepository.save(memberAddress);

        assertThat(saved.getMemberAddressId()).isPositive();
        assertThat(saved.getAddress()).contains("테헤란로");
        assertThat(saved.getMember()).isEqualTo(testMember);
        assertThat(saved.isDefaultAddress()).isTrue();
    }

    @Test
    @DisplayName("MemberAddress 수정")
    void testUpdateAddress() {
        MemberAddress memberAddress = MemberAddress.builder()
                .addressName("선배님 댁")
                .recipientName("장도연")
                .recipientContact("010-2222-2222")
                .address("광주광역시 동구 필문대로")
                .detailAddress("123")
                .defaultAddress(false)
                .member(testMember)
                .build();

        MemberAddress saved = memberAddressRepository.save(memberAddress);

        saved.setAddressName("도연언니 댁");
        saved.setAddress("경기도 고양시 일산동구 월드고양로");
        saved.setDetailAddress("102-65");

        MemberAddress updated = memberAddressRepository.save(saved);

        assertEquals(saved.getMemberAddressId(), updated.getMemberAddressId());
        assertEquals("도연언니 댁", updated.getAddressName());
        assertEquals("장도연", updated.getRecipientName());
        assertEquals("경기도 고양시 일산동구 월드고양로", updated.getAddress());
        assertEquals("102-65", updated.getDetailAddress());
    }

    @Test
    @DisplayName("MemberAddress 삭제")
    void testDeleteAddress() {
        MemberAddress memberAddress = MemberAddress.builder()
                .addressName("삭제할 집")
                .recipientName("김도영")
                .recipientContact("010-0000-0000")
                .address("서울시 강남구 삭제로")
                .detailAddress("1")
                .defaultAddress(false)
                .member(testMember)
                .build();

        MemberAddress saved = memberAddressRepository.save(memberAddress);
        Long id = saved.getMemberAddressId();

        memberAddressRepository.deleteById(id);

        Optional<MemberAddress> deleted = memberAddressRepository.findById(id);
        assertTrue(deleted.isEmpty(), "삭제된 주소는 조회되지 않아야 합니다.");
    }

    @Test
    @DisplayName("Member 기본 배송지 조회")
    void testFindDefaultAddressByMember() {
        MemberAddress defaultAddress = MemberAddress.builder()
                .addressName("기본 집")
                .recipientName("기본 수령인")
                .recipientContact("010-1111-2222")
                .address("서울시 용산구 기본로")
                .detailAddress("123")
                .defaultAddress(true)
                .member(testMember)
                .build();

        memberAddressRepository.save(defaultAddress);

        Optional<MemberAddress> found = memberAddressRepository.findDefaultMemberAddress(testMember.getMemberId());

        assertTrue(found.isPresent(), "기본 배송지가 조회되어야 합니다.");
        assertTrue(found.get().isDefaultAddress(), "조회된 주소는 기본 배송지여야 합니다.");
        assertEquals(testMember.getMemberId(), found.get().getMember().getMemberId());
    }

    @Test
    @DisplayName("Member 모든 주소 조회")
    void testFindAllByMember() {
        MemberAddress addr1 = MemberAddress.builder()
                .addressName("집1")
                .recipientName("유지민")
                .recipientContact("010-1111-1111")
                .address("서울시 강남구")
                .detailAddress("1번지")
                .defaultAddress(false)
                .member(testMember)
                .build();

        MemberAddress addr2 = MemberAddress.builder()
                .addressName("집2")
                .recipientName("유지민")
                .recipientContact("010-2222-2222")
                .address("서울시 강남구")
                .detailAddress("2번지")
                .defaultAddress(false)
                .member(testMember)
                .build();

        memberAddressRepository.save(addr1);
        memberAddressRepository.save(addr2);

        List<MemberAddress> result = memberAddressRepository.findAllByMember_MemberId(testMember.getMemberId());

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isGreaterThanOrEqualTo(2);
        result.forEach(address -> assertEquals(testMember.getMemberId(), address.getMember().getMemberId()));
    }

    @Test
    @DisplayName("멤버의 모든 주소 기본배송지 설정 해제")
    void TestUnsetAllDefaultForMember(){
        MemberAddress addr1 = MemberAddress.builder()
                .addressName("집1")
                .recipientName("유지민")
                .recipientContact("010-1111-1111")
                .address("서울시 강남구")
                .detailAddress("1번지")
                .defaultAddress(false)
                .member(testMember)
                .build();

        MemberAddress addr2 = MemberAddress.builder()
                .addressName("집2")
                .recipientName("유지민")
                .recipientContact("010-2222-2222")
                .address("서울시 강남구")
                .detailAddress("2번지")
                .defaultAddress(true)
                .member(testMember)
                .build();

        MemberAddress addr3 = MemberAddress.builder()
                .addressName("집3")
                .recipientName("유지민")
                .recipientContact("010-3333-3333")
                .address("서울시 강남구")
                .detailAddress("3번지")
                .defaultAddress(false)
                .member(testMember)
                .build();


        memberAddressRepository.save(addr1);
        memberAddressRepository.save(addr2);
        memberAddressRepository.save(addr3);

        memberAddressRepository.unsetAllDefaultForMember(testMember.getMemberId());
        em.flush();
        em.clear();

        List<MemberAddress> result = memberAddressRepository.findAllByMember_MemberId(testMember.getMemberId());

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isGreaterThanOrEqualTo(3);
        result.forEach(address -> assertEquals(testMember.getMemberId(), address.getMember().getMemberId()));
        result.forEach(address -> assertFalse(address.isDefaultAddress()));
    }
}