package com.nhnacademy.illuwa.domain.pointpolicy.repo;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, String> {
}
