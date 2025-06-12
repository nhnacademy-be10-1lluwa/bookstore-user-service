package com.nhnacademy.illuwa.domain.address.repo;

import com.nhnacademy.illuwa.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
