package com.nhnacademy.illuwa.domain.grade.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GradeName {
    BASIC("일반"), ROYAL("로얄"), GOLD("골드"), PLATINUM("플래티넘");

    final private String name;

    GradeName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static GradeName gradeName(String name) {
        for (GradeName gradeName : GradeName.values()) {
            if (gradeName.getName().equals(name)) {
                return gradeName;
            }
        }
        return null;
    }
}
