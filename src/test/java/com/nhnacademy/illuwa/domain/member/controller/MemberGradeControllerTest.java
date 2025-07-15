package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.impl.MemberGradeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberGradeController.class)
class MemberGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberGradeService memberGradeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("등급 전체 재조정 API 성공")
    void updateAllMemberGrade_success() throws Exception {
        List<MemberGradeUpdateRequest> requests = List.of(
                new MemberGradeUpdateRequest(1L, new BigDecimal("110000")),
                new MemberGradeUpdateRequest(2L, new BigDecimal("210000"))
        );

        Mockito.when(memberGradeService.updateGrades(anyList())).thenReturn(2);

        mockMvc.perform(post("/api/members/grades/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(content().string("총 2명의 등급이 갱신되었어요!"));
    }

    @Test
    @DisplayName("등급별 포인트 지급 API 성공")
    void givePointToGrade_success() throws Exception {
        mockMvc.perform(post("/api/members/grades/event-point")
                        .param("grade", "GOLD")
                        .param("point", "1500"))
                .andExpect(status().isCreated());

        Mockito.verify(memberGradeService).givePointsByGrade(GradeName.GOLD, new BigDecimal("1500"));
    }
}