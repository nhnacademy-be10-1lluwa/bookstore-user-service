package com.nhnacademy.illuwa.domain.point.pointpolicy.service;

import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyUpdateRequest;

import java.util.List;

public interface PointPolicyService {
    PointPolicyResponse findByPolicyKey(String policyKey);
    List<PointPolicyResponse> findAllPointPolicy();
    PointPolicyResponse createPointPolicy(PointPolicyCreateRequest request);
    PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request);
    void deletePointPolicy(String policyKey);
}
