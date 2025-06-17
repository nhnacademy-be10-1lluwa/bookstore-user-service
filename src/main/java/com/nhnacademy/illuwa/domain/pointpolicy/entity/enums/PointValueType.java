package com.nhnacademy.illuwa.domain.pointpolicy.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PointValueType {
    RATE, AMOUNT;

    @JsonCreator
    public static PointValueType fromStringIgnoreCase(String value) {
        for (PointValueType type : PointValueType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }


    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
