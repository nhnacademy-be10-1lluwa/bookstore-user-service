package com.nhnacademy.illuwa.domain.grade.exception;

import java.math.BigDecimal;

public class GradeNotFoundException extends RuntimeException {
    public GradeNotFoundException(String gradeName) { super("해당 등급이 존재하지 않아요! " + gradeName);}


    public GradeNotFoundException(BigDecimal netOrderAmount) { super("해당 금액에 해당하는 등급이 없습니다: " + netOrderAmount);
    }
}
