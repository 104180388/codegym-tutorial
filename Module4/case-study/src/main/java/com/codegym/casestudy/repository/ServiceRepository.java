package com.codegym.casestudy.repository;

import com.codegym.casestudy.model.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findBySpecialtyId(Long specialtyId);
    List<Service> findByActiveTrue();
}
