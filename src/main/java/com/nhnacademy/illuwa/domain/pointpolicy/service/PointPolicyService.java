package com.nhnacademy.illuwa.domain.pointpolicy.service;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;

import java.util.List;

public interface PointPolicyService {
    PointPolicyResponse findByPolicyKey(String policyKey);
    List<PointPolicyResponse> findAllPointPolicy();
    PointPolicyResponse createPointPolicy(PointPolicyCreateRequest request);
    PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request);
    void deletePointPolicy(String policyKey);
}
