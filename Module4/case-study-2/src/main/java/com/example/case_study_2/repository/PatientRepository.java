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

    @Query("SELECT p FROM Patient p WHERE p.fullName LIKE %:keyword% OR p.phone LIKE %:keyword%")
    List<Patient> searchByNameOrPhone(@Param("keyword") String keyword);
}
