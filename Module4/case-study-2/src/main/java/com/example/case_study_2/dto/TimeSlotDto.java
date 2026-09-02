package com.example.case_study_2.dto;

public class TimeSlotDto {

    private String timeStr;       // "08:00"
    private String formattedTime; // "08:00 AM"
    private boolean available;    // true/false

    public TimeSlotDto() {
    }

    public TimeSlotDto(String timeStr, String formattedTime, boolean available) {
        this.timeStr = timeStr;
        this.formattedTime = formattedTime;
        this.available = available;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
