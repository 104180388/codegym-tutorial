package com.example.case_study_2.entity.enums;

public enum Shift {
    MORNING("Ca Sáng (08:00 - 11:30)"),
    AFTERNOON("Ca Chiều (13:30 - 16:30)");

    private final String displayName;

    Shift(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
