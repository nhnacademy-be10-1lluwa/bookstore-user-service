package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.service.impl.MemberGradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/members/grades")
@RequiredArgsConstructor
@Tag(name = "회원 등급 API", description = "회원 등급 재조정 및 등급별 포인트 지급 기능")
public class MemberGradeController {
    private final MemberGradeService memberGradeService;

    /**
     * 전체 회원 등급 재조정
     */
    @Operation(summary = "회원 등급 일괄 재조정", description = "관리자가 요청한 회원 리스트의 등급을 재조정합니다.")
    @ApiResponse(responseCode = "200", description = "등급 재조정 성공 (변경된 회원 수 반환)")
    @ApiResponse(responseCode = "400", description = "입력 데이터 오류")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/recalculate")
    public ResponseEntity<Integer> updateAllMemberGrade(@RequestBody List<MemberGradeUpdateRequest> requests) {
        int updatedCount = memberGradeService.updateGrades(requests);
        return ResponseEntity.ok(updatedCount);
    }

    /**
     * 등급별 포인트 지급
     */
    @Operation(summary = "등급별 포인트 지급", description = "특정 등급의 회원에게 동일한 포인트를 일괄 지급합니다.")
    @ApiResponse(responseCode = "201", description = "포인트 지급 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 등급명 또는 포인트 값")
    @ApiResponse(responseCode = "404", description = "해당 등급의 회원 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/{gradeName}/points")
    public ResponseEntity<Void> givePointToGrade(@PathVariable GradeName gradeName, @RequestParam(value = "point") BigDecimal point) {
        memberGradeService.givePointsByGrade(gradeName, point);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}