package com.nhnacademy.illuwa.common.config.init;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.exception.GradeNotFoundException;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Order(3)
public class MemberInitializer implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args){

        Grade basicGrade = gradeRepository.findByGradeName(GradeName.BASIC)
                .orElseThrow(() -> new GradeNotFoundException(GradeName.BASIC.getName()));
        PointPolicy joinPolicy = pointPolicyRepository.findById("join_point")
                .orElseThrow(() -> new PointPolicyNotFoundException("join_point"));


        /* 관리자 계정 */
        if (memberRepository.findByEmail("admin@1lluwa.com").isEmpty()) {
            Member admin = Member.builder()
                    .name("관리자")
                    .birth("1990-01-01")
                    .email("admin@1lluwa.com")
                    .password(passwordEncoder.encode("Admin1234$"))
                    .role(Role.ADMIN)
                    .contact("010-1234-5678")
                    .grade(basicGrade)
                    .point(joinPolicy.getValue())
                    .status(Status.ACTIVE)
                    .build();

            Member adminMember = memberRepository.save(admin);

            PointHistory joinPoint1 = PointHistory.builder()
                    .memberId(adminMember.getMemberId())
                    .type(PointHistoryType.EARN)
                    .amount(joinPolicy.getValue())
                    .reason(PointReason.JOIN)
                    .balance(joinPolicy.getValue())
                    .createdAt(LocalDateTime.now())
                    .build();

            pointHistoryRepository.save(joinPoint1);
        }

        /* 일반 회원 계정 */
        if (memberRepository.findByEmail("karina@naver.com").isEmpty()) {
            Member basicMember = Member.builder()
                    .name("카리나")
                    .birth("2000-04-11")
                    .email("karina@naver.com")
                    .password(passwordEncoder.encode("Karina1234$"))
                    .role(Role.USER)
                    .contact("010-1234-5678")
                    .grade(basicGrade)
                    .point(joinPolicy.getValue())
                    .status(Status.ACTIVE)
                    .build();

            Member savedMember = memberRepository.save(basicMember);

            PointHistory joinPoint2 = PointHistory.builder()
                    .memberId(savedMember.getMemberId())
                    .type(PointHistoryType.EARN)
                    .amount(joinPolicy.getValue())
                    .reason(PointReason.JOIN)
                    .balance(joinPolicy.getValue())
                    .createdAt(LocalDateTime.now())
                    .build();

            pointHistoryRepository.save(joinPoint2);
        }


        /* 휴면 회원 계정 */
        if (memberRepository.findByEmail("inactive@1lluwa.com").isEmpty()) {
            LocalDateTime joinedAt = LocalDateTime.now().minusMonths(6);
            LocalDateTime lastLoginAt = LocalDateTime.now().minusMonths(4);

            Member dormantMember = Member.builder()
                    .name("여름휴가")
                    .birth("2000-01-01")
                    .email("inactive@1lluwa.com")
                    .password(passwordEncoder.encode("Inactive1234$"))
                    .role(Role.USER)
                    .contact("010-9999-8888")
                    .grade(basicGrade)
                    .point(joinPolicy.getValue())
                    .status(Status.INACTIVE)
                    .createdAt(joinedAt)
                    .lastLoginAt(lastLoginAt)
                    .build();

            Member savedDormant = memberRepository.save(dormantMember);

            PointHistory joinPoint3 = PointHistory.builder()
                    .memberId(savedDormant.getMemberId())
                    .type(PointHistoryType.EARN)
                    .amount(joinPolicy.getValue())
                    .reason(PointReason.JOIN)
                    .balance(joinPolicy.getValue())
                    .createdAt(joinedAt)
                    .build();

            pointHistoryRepository.save(joinPoint3);
        }
    }
}
