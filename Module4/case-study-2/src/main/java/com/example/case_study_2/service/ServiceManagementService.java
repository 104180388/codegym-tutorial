package com.example.case_study_2.service;

import com.example.case_study_2.entity.ServiceEntity;
import com.example.case_study_2.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceManagementService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceEntity> getAllActiveServices() {
        return serviceRepository.findByIsActiveTrue();
    }

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ y tế"));
    }

    @Transactional
    public ServiceEntity saveOrUpdateService(ServiceEntity service) {
        if (service.getId() == null && serviceRepository.findByServiceCode(service.getServiceCode()).isPresent()) {
            throw new IllegalArgumentException("Mã dịch vụ đã tồn tại");
        }
        return serviceRepository.save(service);
    }
}
