package com.codegym.casestudy.repository;

import com.codegym.casestudy.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByAccountId(Long accountId);
    List<Doctor> findBySpecialtyId(Long specialtyId);
}
