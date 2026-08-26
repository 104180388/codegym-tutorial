package com.codegym.casestudy.service;

import com.codegym.casestudy.model.dto.ExaminationDTO;
import com.codegym.casestudy.model.entity.Appointment;
import com.codegym.casestudy.model.entity.MedicalRecord;

import java.util.List;

public interface ExaminationService {
    void confirmAppointment(Long appointmentId);
    void checkInAppointment(Long appointmentId);
    MedicalRecord saveExamination(ExaminationDTO dto);
    List<Appointment> getQueueAppointments();
    Appointment getAppointmentDetails(Long appointmentId);
    MedicalRecord getMedicalRecordByAppointment(Long appointmentId);
}
