package com.example.case_study_2.repository;

import com.example.case_study_2.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUserId(Long userId);
    Optional<Staff> findByStaffCode(String staffCode);
}
