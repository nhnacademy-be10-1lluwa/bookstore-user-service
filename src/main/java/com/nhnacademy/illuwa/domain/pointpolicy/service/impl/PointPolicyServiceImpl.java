package com.nhnacademy.illuwa.domain.pointpolicy.service.impl;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.DuplicatePointPolicyException;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
import com.nhnacademy.illuwa.domain.pointpolicy.utils.PointPolicyMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;
    private final PointPolicyMapper pointPolicyMapper;

    @Transactional
    @Override
    public List<PointPolicyResponse> saveAllPointPolicy(List<PointPolicyCreateRequest> requestList) {
        Set<String> uniqueKeys = new HashSet<>();
        for (PointPolicyCreateRequest request : requestList) {
            if (!uniqueKeys.add(request.getPolicyKey())) {   //false 반환 시 중복
                throw new DuplicatePointPolicyException(request.getPolicyKey());
            }
        }

        List<PointPolicy> pointPolicyList = requestList.stream()
                .map(pointPolicyMapper::dtoToEntity)
                .collect(Collectors.toList());

        pointPolicyRepository.saveAll(pointPolicyList);

        return pointPolicyList.stream()
                .map(pointPolicyMapper::entityToDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    @Override
    public PointPolicyResponse findByPolicyKey(String policyKey) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey)
                .orElseThrow(() -> new PointPolicyNotFoundException(policyKey));

        return pointPolicyMapper.entityToDto(pointPolicy);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PointPolicyResponse> findAllPointPolicy() {
        List<PointPolicy> pointPolicyList = pointPolicyRepository.findAll();

        return pointPolicyList.stream()
                .map(pointPolicyMapper::entityToDto)
                .toList();
    }

    @Transactional
    @Override
    public PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey).orElseThrow(() -> new PointPolicyNotFoundException(policyKey));
        PointPolicy updatedPolicy = pointPolicyMapper.updatePointPolicy(pointPolicy, request);

        return pointPolicyMapper.entityToDto(updatedPolicy);
    }
}
