package com.codegym.casestudy.repository;

import com.codegym.casestudy.model.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByNameContainingIgnoreCase(String name);
}
