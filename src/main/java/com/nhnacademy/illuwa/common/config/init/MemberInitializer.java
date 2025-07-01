package com.nhnacademy.illuwa.common.config.init;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.exception.GradeNotFoundException;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Order(2)
public class MemberInitializer implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;

    @Override
    public void run(ApplicationArguments args){
        if (memberRepository.findByEmail("admin@1lluwa.com").isPresent()) {
            return;
        }

        Grade basicGrade = gradeRepository.findByGradeName(GradeName.BASIC)
                .orElseThrow(() -> new GradeNotFoundException(GradeName.BASIC.getName()));

        Member admin = Member.builder()
                .name("관리자")
                .birth("1990-01-01")
                .email("admin@1lluwa.com")
                .password("$admiN1234")  //실제론 인코딩 처리
                .role(Role.ADMIN)
                .contact("010-1234-5678")
                .grade(basicGrade)
                .point(BigDecimal.ZERO)
                .status(Status.ACTIVE)
                .build();

        if (memberRepository.findByEmail("karina@naver.com").isPresent()) {
            return;
        }

        Member basicMember = Member.builder()
                .name("카리나")
                .birth("2000-04-11")
                .email("karina@naver.com")
                .password("$karinA1234")   //실제론 인코딩 처리
                .role(Role.USER)
                .contact("010-1234-5678")
                .grade(basicGrade)
                .point(BigDecimal.ZERO)
                .status(Status.ACTIVE)
                .build();

        memberRepository.save(admin);
        memberRepository.save(basicMember);
    }
}
