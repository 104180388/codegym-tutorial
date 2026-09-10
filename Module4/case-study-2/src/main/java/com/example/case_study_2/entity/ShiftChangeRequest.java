package com.example.case_study_2.entity;

import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.entity.enums.ShiftChangeType;
import com.example.case_study_2.entity.enums.ShiftRequestStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_change_requests")
public class ShiftChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", nullable = true)
    private DoctorSchedule schedule;

    @Column(name = "work_date", nullable = false)
    private LocalDate currentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_shift", nullable = false)
    private Shift currentShift;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private ShiftChangeType requestType;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_shift")
    private Shift targetShift;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_doctor_id")
    private Doctor targetDoctor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftRequestStatus status = ShiftRequestStatus.PENDING;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    public ShiftChangeRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public DoctorSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(DoctorSchedule schedule) {
        this.schedule = schedule;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Shift getCurrentShift() {
        return currentShift;
    }

    public void setCurrentShift(Shift currentShift) {
        this.currentShift = currentShift;
    }

    public ShiftChangeType getRequestType() {
        return requestType;
    }

    public void setRequestType(ShiftChangeType requestType) {
        this.requestType = requestType;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public Shift getTargetShift() {
        return targetShift;
    }

    public void setTargetShift(Shift targetShift) {
        this.targetShift = targetShift;
    }

    public Doctor getTargetDoctor() {
        return targetDoctor;
    }

    public void setTargetDoctor(Doctor targetDoctor) {
        this.targetDoctor = targetDoctor;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ShiftRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftRequestStatus status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
