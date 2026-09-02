package com.example.case_study_2.repository;

import com.example.case_study_2.entity.DoctorSchedule;
import com.example.case_study_2.entity.enums.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctorId(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndWorkDateGreaterThanEqualOrderByWorkDateAsc(Long doctorId, LocalDate startDate);

    List<DoctorSchedule> findByWorkDateGreaterThanEqualOrderByWorkDateAsc(LocalDate startDate);

    List<DoctorSchedule> findByWorkDate(LocalDate workDate);

    List<DoctorSchedule> findByDoctorIdAndWorkDate(Long doctorId, LocalDate workDate);

    Optional<DoctorSchedule> findByDoctorIdAndWorkDateAndShift(Long doctorId, LocalDate workDate, Shift shift);

    List<DoctorSchedule> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
}

