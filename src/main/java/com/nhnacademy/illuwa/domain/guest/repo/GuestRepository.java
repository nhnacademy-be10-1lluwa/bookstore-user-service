package com.nhnacademy.illuwa.domain.guest.repo;

import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findGuestByOrderNumberAndOrderPassword(String orderNumber, String orderPassword);
}
