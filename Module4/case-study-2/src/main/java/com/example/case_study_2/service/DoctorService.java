package com.example.case_study_2.service;

import com.example.case_study_2.entity.Doctor;
import com.example.case_study_2.entity.DoctorSchedule;
import com.example.case_study_2.entity.User;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.repository.DoctorRepository;
import com.example.case_study_2.repository.DoctorScheduleRepository;
import com.example.case_study_2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ"));
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ bác sĩ cho người dùng này"));
    }

    @Transactional
    public void updateDoctorProfile(Long doctorId, String fullName, String email, String phone,
            String degree, Integer experienceYears, String bio) {
        Doctor doctor = getDoctorById(doctorId);
        User user = doctor.getUser();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        userRepository.save(user);

        doctor.setDegree(degree);
        doctor.setExperienceYears(experienceYears);
        doctor.setBio(bio);
        doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsByServiceId(Long serviceId) {
        if (serviceId == null) {
            return getAllDoctors();
        }
        return doctorRepository.findByServicesId(serviceId);
    }

    public List<DoctorSchedule> getDoctorSchedules(Long doctorId) {
        return scheduleRepository.findByDoctorIdAndWorkDateGreaterThanEqualOrderByWorkDateAsc(doctorId, LocalDate.now());
    }

    public List<DoctorSchedule> getAllSchedules() {
        return scheduleRepository.findByWorkDateGreaterThanEqualOrderByWorkDateAsc(LocalDate.now());
    }


    @Transactional
    public DoctorSchedule addOrUpdateSchedule(Long doctorId, LocalDate workDate, Shift shift) {
        Doctor doctor = getDoctorById(doctorId);
        DoctorSchedule schedule = scheduleRepository.findByDoctorIdAndWorkDateAndShift(doctorId, workDate, shift)
                .orElse(new DoctorSchedule());

        schedule.setDoctor(doctor);
        schedule.setWorkDate(workDate);
        schedule.setShift(shift);

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void toggleSchedule(Long doctorId, LocalDate workDate, Shift shift) {
        var existing = scheduleRepository.findByDoctorIdAndWorkDateAndShift(doctorId, workDate, shift);
        if (existing.isPresent()) {
            scheduleRepository.delete(existing.get());
        } else {
            addOrUpdateSchedule(doctorId, workDate, shift);
        }
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}

