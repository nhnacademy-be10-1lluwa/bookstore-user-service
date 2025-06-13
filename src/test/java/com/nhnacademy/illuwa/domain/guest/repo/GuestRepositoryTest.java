package com.nhnacademy.illuwa.domain.guest.repo;

import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GuestRepositoryTest {

    @Autowired
    GuestRepository guestRepository;

    Guest testGuest;

    @BeforeEach
    void SetUp(){
        testGuest = Guest.builder()
                .name("카리나")
                .email("test@email.com")
                .orderPassword("guestPW!")
                .contact("010-2394-0592")
                .orderId(1L)
                .build();

        guestRepository.save(testGuest);
    }

    @Test
    @DisplayName("비회원 정보저장 - 주문 시")
    void testSave(){
        Guest guest = Guest.builder()
                .name("저장된비회원")
                .email("guest@email.com")
                .orderPassword("guestPW1!")
                .contact("010-2394-0592")
                .orderId(2L)
                .build();

        Guest saved = guestRepository.save(guest);

        assertNotNull(saved.getGuestId());
        assertEquals("저장된비회원", saved.getName());
    }

    @Test
    @DisplayName("비회원 정보조회 - 로그인")
    void testFindGuestByOrderIdAndOrderPassword(){
        long orderId = testGuest.getOrderId();
        String orderPassword = testGuest.getOrderPassword();;

        Guest customerGuest = guestRepository.findGuestByOrderIdAndOrderPassword(orderId, orderPassword);

        assertNotNull(customerGuest);
        assertEquals(customerGuest.getGuestId(), testGuest.getGuestId());
        assertEquals("카리나", customerGuest.getName());
    }


}