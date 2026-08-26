package com.codegym.casestudy.service;

import com.codegym.casestudy.model.entity.Specialty;
import java.util.List;
import java.util.Optional;

public interface SpecialtyService {
    List<Specialty> findAll();
    Optional<Specialty> findById(Long id);
    Specialty save(Specialty specialty);
    void deleteById(Long id);
}
