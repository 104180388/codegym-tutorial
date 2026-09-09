package com.example.case_study_2.service;

import com.example.case_study_2.entity.*;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.Gender;
import com.example.case_study_2.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OverdueAppointmentCancellationTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    @Transactional
    void testAutoCancelOverdueAppointments() {
        // Setup a test patient
        Patient patient = patientRepository.findAll().stream().findFirst().orElseGet(() -> {
            Patient p = new Patient();
            p.setFullName("Nguyễn Văn Test");
            p.setPhone("0988776655");
            p.setDob(LocalDate.of(1990, 1, 1));
            p.setGender(Gender.MALE);
            return patientRepository.save(p);
        });

        // Setup a test doctor
        Doctor doctor = doctorRepository.findAll().stream().findFirst().orElse(null);
        // Setup a test service
        ServiceEntity service = serviceRepository.findAll().stream().findFirst().orElse(null);

        if (doctor != null && service != null) {
            // 1. Create an uncompleted past appointment (yesterday) with status PENDING
            Appointment overduePendingApp = new Appointment();
            overduePendingApp.setAppointmentCode("APT-TEST-001");
            overduePendingApp.setPatient(patient);
            overduePendingApp.setDoctor(doctor);
            overduePendingApp.setService(service);
            overduePendingApp.setAppointmentDate(LocalDate.now().minusDays(1));
            overduePendingApp.setAppointmentTime(LocalTime.of(9, 0));
            overduePendingApp.setStatus(AppointmentStatus.PENDING);
            overduePendingApp = appointmentRepository.save(overduePendingApp);

            // 2. Create a completed past appointment (yesterday) with status COMPLETED
            Appointment overdueCompletedApp = new Appointment();
            overdueCompletedApp.setAppointmentCode("APT-TEST-002");
            overdueCompletedApp.setPatient(patient);
            overdueCompletedApp.setDoctor(doctor);
            overdueCompletedApp.setService(service);
            overdueCompletedApp.setAppointmentDate(LocalDate.now().minusDays(1));
            overdueCompletedApp.setAppointmentTime(LocalTime.of(10, 0));
            overdueCompletedApp.setStatus(AppointmentStatus.COMPLETED);
            overdueCompletedApp = appointmentRepository.save(overdueCompletedApp);

            // 3. Execute autoCancelOverdueAppointments
            int cancelledCount = appointmentService.autoCancelOverdueAppointments();
            assertTrue(cancelledCount >= 1);

            // 4. Verify overdue pending app is now CANCELLED
            Appointment updatedPendingApp = appointmentRepository.findById(overduePendingApp.getId()).orElse(null);
            assertNotNull(updatedPendingApp);
            assertEquals(AppointmentStatus.CANCELLED, updatedPendingApp.getStatus());
            assertNotNull(updatedPendingApp.getCancellationReason());

            // 5. Verify overdue completed app is still COMPLETED
            Appointment updatedCompletedApp = appointmentRepository.findById(overdueCompletedApp.getId()).orElse(null);
            assertNotNull(updatedCompletedApp);
            assertEquals(AppointmentStatus.COMPLETED, updatedCompletedApp.getStatus());
        }
    }
}
