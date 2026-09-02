package com.example.case_study_2.repository;

import com.example.case_study_2.entity.ExaminationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationRecordRepository extends JpaRepository<ExaminationRecord, Long> {
    Optional<ExaminationRecord> findByAppointmentId(Long appointmentId);

    @Query("SELECT e FROM ExaminationRecord e WHERE e.appointment.patient.id = :patientId ORDER BY e.createdAt DESC")
    List<ExaminationRecord> findByPatientId(@Param("patientId") Long patientId);
}
