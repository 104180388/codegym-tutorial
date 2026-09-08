package com.example.case_study_2.entity.enums;

public enum AppointmentStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    CHECKED_IN("Đã tiếp nhận"),
    IN_PROGRESS("Đang khám"),
    AWAITING_PAYMENT("Chờ thanh toán"),
    COMPLETED("Đã hoàn thành"),
    CANCELLED("Đã hủy");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
