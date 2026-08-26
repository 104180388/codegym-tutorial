package com.codegym.casestudy.service;

import com.codegym.casestudy.model.entity.Doctor;
import com.codegym.casestudy.model.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> findAll();
    Optional<Doctor> findById(Long id);
    Doctor save(Doctor doctor);
    void deleteById(Long id);
    Schedule createSchedule(Long doctorId, LocalDate workDate, String timeSlot, Integer maxPatients);
    List<Schedule> findSchedulesByDoctor(Long doctorId);
}
