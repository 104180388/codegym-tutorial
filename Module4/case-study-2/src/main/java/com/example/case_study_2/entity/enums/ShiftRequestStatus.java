package com.example.case_study_2.entity.enums;

public enum ShiftRequestStatus {
    PENDING("Chờ phê duyệt"),
    APPROVED("Đã phê duyệt"),
    REJECTED("Đã từ chối"),
    CANCELLED("Đã hủy");

    private final String displayName;

    ShiftRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
