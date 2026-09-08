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

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status IN (com.example.case_study_2.entity.enums.AppointmentStatus.PENDING, com.example.case_study_2.entity.enums.AppointmentStatus.CONFIRMED, com.example.case_study_2.entity.enums.AppointmentStatus.CHECKED_IN, com.example.case_study_2.entity.enums.AppointmentStatus.IN_PROGRESS, com.example.case_study_2.entity.enums.AppointmentStatus.AWAITING_PAYMENT, com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED) ORDER BY a.appointmentDate DESC, a.appointmentTime DESC, a.id DESC")
    List<Appointment> findPatientAppointmentsActiveAndCompleted(@Param("patientId") Long patientId);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusIn(Long doctorId, LocalDate appointmentDate, List<AppointmentStatus> statuses);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :startDate ORDER BY a.appointmentDate ASC, a.appointmentTime ASC, a.id ASC")
    List<Appointment> findDoctorAppointmentsFromDate(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT a FROM Appointment a ORDER BY CASE WHEN a.status = com.example.case_study_2.entity.enums.AppointmentStatus.PENDING THEN 0 ELSE 1 END ASC, a.appointmentDate DESC, a.appointmentTime DESC, a.id DESC")
    List<Appointment> findAllForStaffManagement();

    List<Appointment> findAllByOrderByAppointmentDateAscAppointmentTimeAsc();
    List<Appointment> findAllByOrderByIdDesc();
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
