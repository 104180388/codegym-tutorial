package com.example.case_study_2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExaminationDto {

    @NotNull(message = "Lịch hẹn không hợp lệ")
    private Long appointmentId;

    @NotBlank(message = "Chẩn đoán không được để trống")
    private String diagnosis;

    private String treatmentPlan;
    private String prescriptionAdvice;
    private String resultAttachmentUrl;
    private String doctorNotes;

    public ExaminationDto() {
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getPrescriptionAdvice() {
        return prescriptionAdvice;
    }

    public void setPrescriptionAdvice(String prescriptionAdvice) {
        this.prescriptionAdvice = prescriptionAdvice;
    }

    public String getResultAttachmentUrl() {
        return resultAttachmentUrl;
    }

    public void setResultAttachmentUrl(String resultAttachmentUrl) {
        this.resultAttachmentUrl = resultAttachmentUrl;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
