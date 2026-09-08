package com.example.case_study_2.repository;

import com.example.case_study_2.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserId(Long userId);
    Optional<Patient> findByPhone(String phone);

    @Query("SELECT p FROM Patient p WHERE p.fullName LIKE %:keyword% OR p.phone LIKE %:keyword% ORDER BY p.id DESC")
    List<Patient> searchByNameOrPhone(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status IN (com.example.case_study_2.entity.enums.AppointmentStatus.IN_PROGRESS, com.example.case_study_2.entity.enums.AppointmentStatus.AWAITING_PAYMENT, com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED) ORDER BY a.patient.fullName ASC")
    List<Patient> findPatientsExaminedByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status IN (com.example.case_study_2.entity.enums.AppointmentStatus.IN_PROGRESS, com.example.case_study_2.entity.enums.AppointmentStatus.AWAITING_PAYMENT, com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED) AND (a.patient.fullName LIKE %:keyword% OR a.patient.phone LIKE %:keyword%) ORDER BY a.patient.fullName ASC")
    List<Patient> searchPatientsExaminedByDoctor(@Param("doctorId") Long doctorId, @Param("keyword") String keyword);

    List<Patient> findAllByOrderByIdDesc();
}
