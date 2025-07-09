package com.nhnacademy.illuwa.common.annotation;

import jakarta.validation.*;

import java.util.regex.Pattern;

public class ConditionalPatternValidator implements ConstraintValidator<ConditionalPattern, String> {
    private Pattern pattern;

    @Override
    public void initialize(ConditionalPattern annotation) {
        pattern = Pattern.compile(annotation.regexp());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return pattern.matcher(value).matches();
    }
}
