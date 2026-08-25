package com.codegym.casestudy.repository;

import com.codegym.casestudy.model.entity.AppointmentServices;
import com.codegym.casestudy.model.entity.AppointmentServicesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentServicesRepository extends JpaRepository<AppointmentServices, AppointmentServicesId> {
    List<AppointmentServices> findByAppointmentId(Long appointmentId);
    List<AppointmentServices> findByServiceId(Long serviceId);
    void deleteByAppointmentId(Long appointmentId);
}
