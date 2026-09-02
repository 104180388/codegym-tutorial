package com.example.case_study_2.repository;

import com.example.case_study_2.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByIsActiveTrue();
    Optional<ServiceEntity> findByServiceCode(String serviceCode);
}
