package com.nhnacademy.illuwa.domain.pointpolicy.service;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;

import java.util.List;

public interface PointPolicyService {
    List<PointPolicyResponse> saveAllPointPolicy(List<PointPolicyCreateRequest> requestList);
    PointPolicyResponse findByPolicyKey(String policyKey);
    List<PointPolicyResponse> findAllPointPolicy();
    PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request);
}
