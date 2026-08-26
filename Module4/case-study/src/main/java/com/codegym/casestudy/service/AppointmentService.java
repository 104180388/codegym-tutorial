package com.codegym.casestudy.service;

import com.codegym.casestudy.model.dto.AppointmentDTO;
import com.codegym.casestudy.model.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    boolean isScheduleAvailable(Long scheduleId);
    Appointment createAppointment(AppointmentDTO dto, String username);
    List<Appointment> getPatientAppointments(String username);
    List<Appointment> findByPatientId(Long patientId);
    void cancelAppointment(Long appointmentId);
}
