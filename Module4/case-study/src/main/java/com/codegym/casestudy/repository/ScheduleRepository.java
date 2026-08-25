package com.codegym.casestudy.repository;

import com.codegym.casestudy.model.entity.Schedule;
import com.codegym.casestudy.model.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDoctorIdAndWorkDate(Long doctorId, LocalDate workDate);
    List<Schedule> findByDoctorIdAndWorkDateGreaterThanEqual(Long doctorId, LocalDate workDate);
    List<Schedule> findByStatus(ScheduleStatus status);
}
