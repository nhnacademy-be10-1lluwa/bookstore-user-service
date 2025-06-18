package com.nhnacademy.illuwa.domain.guest.repo;

import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    Guest findGuestByOrderNumberAndOrderPassword(String orderNumber, String orderPassword);
}
