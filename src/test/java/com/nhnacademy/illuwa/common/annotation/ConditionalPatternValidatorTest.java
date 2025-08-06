package com.nhnacademy.illuwa.common.annotation;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

class ConditionalPatternValidatorTest {

    ConditionalPatternValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ConditionalPatternValidator();

        ConditionalPattern annotation = new ConditionalPattern() {
            @Override
            public String regexp() {
                return "\\d{3}-\\d{4}";
            }

            @Override
            public String message() {
                return "Invalid format";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return ConditionalPattern.class;
            }
        };

        validator.initialize(annotation);
    }

    @Test
    void nullValueIsValid() {
        assertTrue(validator.isValid(null, null), "null 값은 유효해야 합니다.");
    }

    @Test
    void validPatternMatches() {
        assertTrue(validator.isValid("123-4567", null), "패턴에 맞는 문자열은 유효해야 합니다.");
    }

    @Test
    void invalidPatternDoesNotMatch() {
        assertFalse(validator.isValid("abc-1234", null), "패턴에 맞지 않는 문자열은 유효하지 않아야 합니다.");
        assertFalse(validator.isValid("1234567", null), "하이픈 없으면 유효하지 않아야 합니다.");
    }
}
