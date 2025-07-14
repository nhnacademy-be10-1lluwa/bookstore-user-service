package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.service.impl.MemberGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/members/grades")
@RequiredArgsConstructor
public class MemberGradeController {
    private final MemberGradeService memberGradeService;

    /**
    * 전체회원 등급 재조정
    */
    @PostMapping("/update")
    public ResponseEntity<String> updateAllMemberGrade(@RequestBody List<MemberGradeUpdateRequest> requests) {
        int updatedCount = memberGradeService.updateGrades(requests);
        return ResponseEntity.ok("총 " + updatedCount + "명의 등급이 갱신되었어요!");
    }

    /**
     * 등급별 포인트 지급
     */
    @PostMapping("/event-point")
    public ResponseEntity<Void> givePointToGrade(@RequestParam(value = "grade") GradeName gradeName, @RequestParam(value = "point") BigDecimal point) {
        memberGradeService.givePointsByGrade(gradeName, point);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
