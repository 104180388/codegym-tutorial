package com.example.case_study_2.dto;

public class AiDiagnoseRequestDto {
    private Long appointmentId;
    private String symptoms;
    private String patientName;
    private String gender;
    private Integer age;
    private String serviceName;

    public AiDiagnoseRequestDto() {
    }

    public AiDiagnoseRequestDto(Long appointmentId, String symptoms, String patientName, String gender, Integer age, String serviceName) {
        this.appointmentId = appointmentId;
        this.symptoms = symptoms;
        this.patientName = patientName;
        this.gender = gender;
        this.age = age;
        this.serviceName = serviceName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
