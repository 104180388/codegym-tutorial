package com.example.case_study_2.dto;

public class AiDiagnoseResponseDto {
    private boolean success;
    private String message;
    private String diagnosis;
    private String treatmentPlan;
    private String prescriptionAdvice;
    private String doctorNotes;
    private String source;

    public AiDiagnoseResponseDto() {
    }

    public AiDiagnoseResponseDto(boolean success, String message, String diagnosis, String treatmentPlan, String prescriptionAdvice, String doctorNotes, String source) {
        this.success = success;
        this.message = message;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.prescriptionAdvice = prescriptionAdvice;
        this.doctorNotes = doctorNotes;
        this.source = source;
    }

    public static AiDiagnoseResponseDto success(String diagnosis, String treatmentPlan, String prescriptionAdvice, String doctorNotes, String source) {
        return new AiDiagnoseResponseDto(true, "AI đã phân tích triệu chứng thành công", diagnosis, treatmentPlan, prescriptionAdvice, doctorNotes, source);
    }

    public static AiDiagnoseResponseDto error(String message) {
        return new AiDiagnoseResponseDto(false, message, null, null, null, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
