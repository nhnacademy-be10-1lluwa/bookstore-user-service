package com.nhnacademy.illuwa.domain.member.dto;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterResponse {
    private Long memberId;
    private String email;
    private String name;
    private String message;

    public MemberRegisterResponse(Member member, String message){
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.message = message;
    }
}
