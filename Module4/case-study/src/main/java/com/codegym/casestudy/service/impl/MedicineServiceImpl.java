package com.codegym.casestudy.service.impl;

import com.codegym.casestudy.model.entity.Medicine;
import com.codegym.casestudy.repository.MedicineRepository;
import com.codegym.casestudy.service.MedicineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medicine> findAll() {
        return medicineRepository.findAll();
    }

    @Override
    public Optional<Medicine> findById(Long id) {
        return medicineRepository.findById(id);
    }

    @Override
    public Medicine save(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Override
    public void deleteById(Long id) {
        medicineRepository.deleteById(id);
    }
}
