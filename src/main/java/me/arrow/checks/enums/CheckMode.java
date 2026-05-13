package me.arrow.checks.enums;

import lombok.Getter;

@Getter
public enum CheckMode {

    FLAG("FLAG"),
    MITIGATE("MITIGATE"),
    BOTH("BOTH");

    private final String checkMode;

    CheckMode(String checkMode) {
        this.checkMode = checkMode;
    }
}
