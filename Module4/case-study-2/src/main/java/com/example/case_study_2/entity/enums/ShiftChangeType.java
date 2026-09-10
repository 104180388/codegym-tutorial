package com.example.case_study_2.entity.enums;

public enum ShiftChangeType {
    LEAVE("Xin nghỉ ca"),
    SWAP("Xin đổi ca làm việc");

    private final String displayName;

    ShiftChangeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
