package com.nhnacademy.illuwa.domain.pointpolicy.service.impl;

import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.DuplicatePointPolicyException;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
import com.nhnacademy.illuwa.domain.pointpolicy.utils.PointPolicyMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;
    private final PointPolicyMapper pointPolicyMapper;

    @Transactional(readOnly = true)
    @Override
    public PointPolicyResponse findByPolicyKey(String policyKey) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey)
                .orElseThrow(() -> new PointPolicyNotFoundException(policyKey));

        return pointPolicyMapper.toDto(pointPolicy);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PointPolicyResponse> findAllPointPolicy() {
        List<PointPolicy> pointPolicyList = pointPolicyRepository.findAll();

        return pointPolicyList.stream()
                .map(pointPolicyMapper::toDto)
                .toList();
    }

    @Override
    public PointPolicyResponse createPointPolicy(PointPolicyCreateRequest request) {
        List<PointPolicy> pointPolicyList = pointPolicyRepository.findAll();
        for (PointPolicy pointPolicy : pointPolicyList) {
            if (pointPolicy.getPolicyKey().equals(request.getPolicyKey())) {   //false 반환 시 중복
                throw new DuplicatePointPolicyException(request.getPolicyKey());
            }
        }
        PointPolicy saved = pointPolicyRepository.save(pointPolicyMapper.toEntity(request));
        return pointPolicyMapper.toDto(saved);
    }

    @Override
    public PointPolicyResponse updatePointPolicy(String policyKey, PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey)
                .orElseThrow(() -> new PointPolicyNotFoundException(policyKey));

        //타입 검증
        validatePointPolicyValue(request);

        if(request.getValue() != null){
            pointPolicy.changeValue(request.getValue());
        }
        if(request.getValueType() != null){
            pointPolicy.changeValueType(request.getValueType());
        }
        if(request.getDescription() != null){
            pointPolicy.changeDescription(request.getDescription());
        }
        return pointPolicyMapper.toDto(pointPolicyRepository.save(pointPolicy));
    }

    @Override
    public void deletePointPolicy(String policyKey){
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyKey)
                .orElseThrow(()->
                new PointPolicyNotFoundException(policyKey)
        );
        pointPolicyRepository.deleteById(pointPolicy.getPolicyKey());
    }

    private void validatePointPolicyValue(PointPolicyUpdateRequest request) {
        BigDecimal value = request.getValue();
        PointValueType type = request.getValueType();

        if (value == null || type == null) {
            throw new InvalidInputException("value와 valueType은 빈 값일 수 없습니다.");
        }

        switch (type) {
            case RATE -> {
                // 0보다 크고 1보다 작거나 같은 값만 허용
                if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(BigDecimal.ONE) > 0) {
                    throw new InvalidInputException("RATE 타입은 0보다 크고 1.0 이하의 값만 허용됩니다.");
                }
            }
            case AMOUNT -> {
                // 0 이상만 허용
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("AMOUNT 타입은 0 이상만 허용됩니다.");
                }
            }
            default -> throw new InvalidInputException("알 수 없는 valueType이에요.");
        }
    }
}
