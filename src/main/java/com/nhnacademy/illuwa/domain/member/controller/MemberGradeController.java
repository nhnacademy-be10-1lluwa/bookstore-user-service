package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.service.impl.MemberGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members/grades/update")
@RequiredArgsConstructor
public class MemberGradeController {
    private final MemberGradeService memberGradeService;

    // 전체회원 등급 재조정
    @PostMapping
    public ResponseEntity<String> updateAllMemberGrade(@RequestBody List<MemberGradeUpdateRequest> requests) {
        int updatedCount = memberGradeService.updateGrades(requests);
        return ResponseEntity.ok("총 " + updatedCount + "명의 등급이 갱신되었어요!");
    }
}
