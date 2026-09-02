package com.example.case_study_2.repository;

import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByAppointmentCode(String appointmentCode);
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusIn(Long doctorId, LocalDate appointmentDate, List<AppointmentStatus> statuses);
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime AND a.status != 'CANCELLED'")
    long countBookedSlot(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate, @Param("appointmentTime") LocalTime appointmentTime);

    @Query("SELECT MAX(a.queueNumber) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate")
    Integer findMaxQueueNumberForDoctorAndDate(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status NOT IN ('COMPLETED', 'CANCELLED')")
    long countUnfinishedAppointmentsByPatient(@Param("patientId") Long patientId);

    @Query("SELECT a.service.name, COUNT(a) FROM Appointment a GROUP BY a.service.name")
    List<Object[]> countAppointmentsByService();
}
