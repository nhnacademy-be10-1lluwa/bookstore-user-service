package com.nhnacademy.illuwa.domain.guest.repo;

import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
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
                .orderPassword("guestPW!")
                .contact("010-2394-0592")
                .orderNumber("20250819063812-234877")
                .build();
    }

    @Test
    @DisplayName("비회원 정보저장 - 주문 시")
    void testSave(){
        Guest saved = guestRepository.save(testGuest);
        assertEquals(testGuest.getGuestId(), saved.getGuestId());
        assertEquals(testGuest.getName(), saved.getName());
        assertEquals(testGuest.getEmail(), saved.getEmail());
        assertEquals(testGuest.getOrderPassword(), saved.getOrderPassword());
        assertEquals(testGuest.getContact(), saved.getContact());
        assertEquals(testGuest.getOrderNumber(), saved.getOrderNumber());
    }

    @Test
    @DisplayName("비회원 정보조회 - 로그인 성공")
    void testFindGuestByOrderIdAndOrderPassword() {
        Guest savedGuest = guestRepository.save(testGuest);

        Optional<Guest> optionalGuest = guestRepository.findGuestByOrderNumberAndOrderPassword(
                savedGuest.getOrderNumber(),
                savedGuest.getOrderPassword()
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

        Optional<Guest> result = guestRepository.findGuestByOrderNumberAndOrderPassword(
                "wrongOrderNumber", "wrongPassword");

        assertTrue(result.isEmpty());
    }
}