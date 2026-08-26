package com.codegym.casestudy.service.impl;

import com.codegym.casestudy.model.entity.Doctor;
import com.codegym.casestudy.model.entity.Schedule;
import com.codegym.casestudy.model.enums.ScheduleStatus;
import com.codegym.casestudy.repository.DoctorRepository;
import com.codegym.casestudy.repository.ScheduleRepository;
import com.codegym.casestudy.service.DoctorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, ScheduleRepository scheduleRepository) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    @Override
    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public void deleteById(Long id) {
        doctorRepository.deleteById(id);
    }

    @Override
    public Schedule createSchedule(Long doctorId, LocalDate workDate, String timeSlot, Integer maxPatients) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ với ID: " + doctorId));

        Schedule schedule = Schedule.builder()
                .doctor(doctor)
                .workDate(workDate)
                .timeSlot(timeSlot)
                .maxPatients(maxPatients != null ? maxPatients : 10)
                .currentPatients(0)
                .status(ScheduleStatus.AVAILABLE)
                .build();

        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> findSchedulesByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorIdAndWorkDateGreaterThanEqual(doctorId, LocalDate.now());
    }
}
