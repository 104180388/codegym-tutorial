package com.example.case_study_2.repository;

import com.example.case_study_2.entity.ShiftChangeRequest;
import com.example.case_study_2.entity.enums.ShiftRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftChangeRequestRepository extends JpaRepository<ShiftChangeRequest, Long> {

    List<ShiftChangeRequest> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    List<ShiftChangeRequest> findByDoctorIdAndStatusOrderByCreatedAtDesc(Long doctorId, ShiftRequestStatus status);

    List<ShiftChangeRequest> findByStatusOrderByCreatedAtDesc(ShiftRequestStatus status);

    List<ShiftChangeRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(ShiftRequestStatus status);

    Optional<ShiftChangeRequest> findByDoctorIdAndScheduleIdAndStatus(Long doctorId, Long scheduleId, ShiftRequestStatus status);

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE ShiftChangeRequest r SET r.schedule = null WHERE r.schedule.id = :scheduleId")
    void unlinkScheduleByScheduleId(@org.springframework.data.repository.query.Param("scheduleId") Long scheduleId);
}
