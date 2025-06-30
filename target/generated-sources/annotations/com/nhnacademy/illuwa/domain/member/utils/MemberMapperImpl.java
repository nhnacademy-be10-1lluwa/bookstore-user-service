package com.nhnacademy.illuwa.domain.member.utils;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-30T18:04:26+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberRegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.name( request.getName() );
        member.birth( request.getBirth() );
        member.email( request.getEmail() );
        member.password( request.getPassword() );
        member.contact( request.getContact() );

        return member.build();
    }

    @Override
    public MemberResponse toDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponse.MemberResponseBuilder memberResponse = MemberResponse.builder();

        GradeName gradeName = memberGradeGradeName( member );
        if ( gradeName != null ) {
            memberResponse.gradeName( gradeName.name() );
        }
        memberResponse.memberId( member.getMemberId() );
        memberResponse.name( member.getName() );
        memberResponse.birth( member.getBirth() );
        memberResponse.email( member.getEmail() );
        memberResponse.role( member.getRole() );
        memberResponse.contact( member.getContact() );
        memberResponse.point( member.getPoint() );
        memberResponse.status( member.getStatus() );
        memberResponse.lastLoginAt( member.getLastLoginAt() );

        return memberResponse.build();
    }

    @Override
    public Member updateMember(Member target, MemberUpdateRequest source) {
        if ( source == null ) {
            return target;
        }

        if ( source.getName() != null ) {
            target.setName( source.getName() );
        }
        if ( source.getPassword() != null ) {
            target.setPassword( source.getPassword() );
        }
        if ( source.getContact() != null ) {
            target.setContact( source.getContact() );
        }

        return target;
    }

    private GradeName memberGradeGradeName(Member member) {
        Grade grade = member.getGrade();
        if ( grade == null ) {
            return null;
        }
        return grade.getGradeName();
    }
}
