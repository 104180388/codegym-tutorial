package com.example.case_study_2.service;

import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.repository.AppointmentRepository;
import com.example.case_study_2.repository.DoctorRepository;
import com.example.case_study_2.repository.InvoiceRepository;
import com.example.case_study_2.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        BigDecimal totalRevenue = invoiceRepository.calculateTotalPaidRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        long totalAppointments = appointmentRepository.count();
        stats.put("totalAppointments", totalAppointments);

        long totalPatients = patientRepository.count();
        stats.put("totalPatients", totalPatients);

        long totalDoctors = doctorRepository.count();
        stats.put("totalDoctors", totalDoctors);

        long pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING).size();
        stats.put("pendingAppointments", pendingAppointments);

        long completedAppointments = appointmentRepository.findByStatus(AppointmentStatus.COMPLETED).size();
        stats.put("completedAppointments", completedAppointments);

        long cancelledAppointments = appointmentRepository.findByStatus(AppointmentStatus.CANCELLED).size();
        stats.put("cancelledAppointments", cancelledAppointments);

        List<Object[]> serviceStats = appointmentRepository.countAppointmentsByService();
        stats.put("serviceStats", serviceStats);

        return stats;
    }
}
