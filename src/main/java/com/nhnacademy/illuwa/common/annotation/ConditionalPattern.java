package com.nhnacademy.illuwa.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalPatternValidator.class)
public @interface ConditionalPattern {
    String message() default "패턴 불일치";
    String regexp();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
