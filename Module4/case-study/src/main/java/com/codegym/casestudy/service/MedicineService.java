package com.codegym.casestudy.service;

import com.codegym.casestudy.model.entity.Medicine;
import java.util.List;
import java.util.Optional;

public interface MedicineService {
    List<Medicine> findAll();
    Optional<Medicine> findById(Long id);
    Medicine save(Medicine medicine);
    void deleteById(Long id);
}
