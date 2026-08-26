package com.codegym.casestudy.service;

import com.codegym.casestudy.model.entity.Service;
import java.util.List;
import java.util.Optional;

public interface MedicalServiceService {
    List<Service> findAll();
    Optional<Service> findById(Long id);
    Service save(Service service);
    void deleteById(Long id);
}
