package com.nhnacademy.illuwa.domain.pointpolicy.service.impl;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
import com.nhnacademy.illuwa.domain.pointpolicy.utils.PointPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {
    @Autowired
    private final PointPolicyRepository pointPolicyRepository;
    @Autowired
    private final PointPolicyMapper pointPolicyMapper;

    @Override
    public PointPolicyResponse getPointPolicy(String policyKey) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey)
                .orElseThrow(PointPolicyNotFoundException::new);

        return pointPolicyMapper.pointPolicyToDto(pointPolicy);
    }

    @Override
    public List<PointPolicyResponse> getAllPointPolicy() {
        List<PointPolicy> pointPolicyList = pointPolicyRepository.findAll();

        return pointPolicyList.stream()
                .map(pointPolicyMapper::pointPolicyToDto)
                .toList();
    }


    @Override
    public PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey).orElseThrow(PointPolicyNotFoundException::new);
        pointPolicyMapper.updatePointPolicy(pointPolicy, request);
        return pointPolicyMapper.pointPolicyToDto(pointPolicy);
    }
}
