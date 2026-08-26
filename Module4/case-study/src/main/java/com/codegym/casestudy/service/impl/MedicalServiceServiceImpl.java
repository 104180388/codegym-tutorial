package com.codegym.casestudy.service.impl;

import com.codegym.casestudy.model.entity.Service;
import com.codegym.casestudy.repository.ServiceRepository;
import com.codegym.casestudy.service.MedicalServiceService;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final ServiceRepository serviceRepository;

    public MedicalServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Service> findAll() {
        return serviceRepository.findAll();
    }

    @Override
    public Optional<Service> findById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public Service save(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public void deleteById(Long id) {
        serviceRepository.deleteById(id);
    }
}
