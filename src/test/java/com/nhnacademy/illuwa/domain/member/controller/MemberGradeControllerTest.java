package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.IdemService;
import com.nhnacademy.illuwa.domain.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.impl.MemberGradeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberGradeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberGradeService memberGradeService;

    @MockBean
    private IdemService idemService;

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

        Mockito.when(idemService.run(Mockito.eq("test-key-1"), Mockito.any(Supplier.class)))
                        .thenAnswer(inv -> ((Supplier<Integer>) inv.getArgument(1)).get());

        mockMvc.perform(post("/api/members/grades/recalculate")
                        .header("Idempotency-Key", "test-key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                        .andExpect(content().string("2"));
        Mockito.verify(memberGradeService, Mockito.times(1)).updateGrades(anyList());
    }

    @Test
    @DisplayName("등급별 포인트 지급 API 성공")
    void givePointToGrade_success() throws Exception {
        mockMvc.perform(post("/api/members/grades/{gradeName}/points", "GOLD")
                        .header("Idempotency-Key", "test-key-1")
                        .param("point", "1500"))
                .andExpect(status().isCreated());

        Mockito.verify(memberGradeService).givePointsByGrade(GradeName.GOLD, new BigDecimal("1500"));
    }

}