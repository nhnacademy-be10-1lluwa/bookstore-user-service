package com.nhnacademy.illuwa.domain.guest.repo;

import com.nhnacademy.illuwa.common.config.JPAConfig;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JPAConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GuestRepositoryTest {

    @Autowired
    GuestRepository guestRepository;

    Guest testGuest;

    @BeforeEach
    void SetUp() {
        testGuest = Guest.builder()
                .guestId("123456789101112131415161718")
                .name("비회원카리나")
                .email("test@email.com")
                .contact("010-2394-0592")
                .orderNumber("20250819063812-234877")
                .orderPassword("orderPassword1234$")
                .build();
    }

    @Test
    @DisplayName("비회원 정보저장 - 주문 시")
    void testSave(){
        Guest saved = guestRepository.save(testGuest);
        assertEquals(testGuest.getGuestId(), saved.getGuestId());
        assertEquals(testGuest.getName(), saved.getName());
        assertEquals(testGuest.getEmail(), saved.getEmail());
        assertEquals(testGuest.getContact(), saved.getContact());
        assertEquals(testGuest.getOrderNumber(), saved.getOrderNumber());
    }

    @Test
    @DisplayName("비회원 정보조회 - 로그인 성공")
    void testFindGuestByOrderNumber() {
        Guest savedGuest = guestRepository.save(testGuest);

        Optional<Guest> optionalGuest = guestRepository.findGuestByOrderNumber(
                savedGuest.getOrderNumber()
        );

        assertTrue(optionalGuest.isPresent(), "Guest should be found!");

        GuestResponse guestDto = GuestResponse.from(optionalGuest.get());

        assertNotNull(guestDto);
        assertEquals(savedGuest.getGuestId(), guestDto.getGuestId());
        assertEquals(savedGuest.getOrderNumber(), guestDto.getOrderNumber());
        assertEquals(savedGuest.getOrderPassword(), optionalGuest.get().getOrderPassword());
    }


    @Test
    @DisplayName("비회원 정보조회 - 로그인 실패")
    void testFindGuestByInvalidOrderNumberAndPassword(){
        guestRepository.save(testGuest);

        Optional<Guest> result = guestRepository.findGuestByOrderNumber(
                "wrongOrderNumber");

        assertTrue(result.isEmpty());
    }
}