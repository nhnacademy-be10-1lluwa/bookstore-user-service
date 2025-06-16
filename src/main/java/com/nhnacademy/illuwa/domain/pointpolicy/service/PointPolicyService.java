package com.nhnacademy.illuwa.domain.pointpolicy.service;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;

import java.util.List;

public interface PointPolicyService {
    PointPolicyResponse getPointPolicy(String policyKey);
    List<PointPolicyResponse> getAllPointPolicy();
    PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request);
}
