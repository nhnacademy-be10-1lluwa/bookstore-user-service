package com.nhnacademy.illuwa.domain.point.pointpolicy.repo;

import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, String> {
}
