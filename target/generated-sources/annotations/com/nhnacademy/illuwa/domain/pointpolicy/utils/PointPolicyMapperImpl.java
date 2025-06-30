package com.nhnacademy.illuwa.domain.pointpolicy.utils;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-30T18:04:26+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class PointPolicyMapperImpl implements PointPolicyMapper {

    @Override
    public PointPolicy dtoToEntity(PointPolicyCreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        PointPolicy.PointPolicyBuilder pointPolicy = PointPolicy.builder();

        pointPolicy.policyKey( dto.getPolicyKey() );
        pointPolicy.value( dto.getValue() );
        pointPolicy.valueType( dto.getValueType() );
        pointPolicy.description( dto.getDescription() );

        return pointPolicy.build();
    }

    @Override
    public PointPolicy dtoToEntity(PointPolicyResponse dto) {
        if ( dto == null ) {
            return null;
        }

        PointPolicy.PointPolicyBuilder pointPolicy = PointPolicy.builder();

        pointPolicy.policyKey( dto.getPolicyKey() );
        pointPolicy.value( dto.getValue() );
        pointPolicy.valueType( dto.getValueType() );
        pointPolicy.description( dto.getDescription() );

        return pointPolicy.build();
    }

    @Override
    public PointPolicyResponse entityToDto(PointPolicy pointPolicy) {
        if ( pointPolicy == null ) {
            return null;
        }

        PointPolicyResponse pointPolicyResponse = new PointPolicyResponse();

        pointPolicyResponse.setPolicyKey( pointPolicy.getPolicyKey() );
        pointPolicyResponse.setValue( pointPolicy.getValue() );
        pointPolicyResponse.setValueType( pointPolicy.getValueType() );
        pointPolicyResponse.setDescription( pointPolicy.getDescription() );

        return pointPolicyResponse;
    }

    @Override
    public PointPolicy updatePointPolicy(PointPolicy target, PointPolicyUpdateRequest request) {
        if ( request == null ) {
            return target;
        }

        if ( request.getValue() != null ) {
            target.setValue( request.getValue() );
        }
        if ( request.getValueType() != null ) {
            target.setValueType( request.getValueType() );
        }
        if ( request.getDescription() != null ) {
            target.setDescription( request.getDescription() );
        }

        return target;
    }
}
