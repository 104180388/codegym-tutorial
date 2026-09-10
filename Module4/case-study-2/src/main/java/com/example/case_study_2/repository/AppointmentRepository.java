package com.example.case_study_2.repository;

import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.Shift;
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
    
    @Query("SELECT a FROM Appointment a WHERE a.status = com.example.case_study_2.entity.enums.AppointmentStatus.PENDING ORDER BY a.appointmentDate ASC, a.appointmentTime ASC, a.id ASC")
    List<Appointment> findPendingAppointments();

    @Query("SELECT a FROM Appointment a WHERE a.status = com.example.case_study_2.entity.enums.AppointmentStatus.CHECKED_IN ORDER BY a.appointmentDate DESC, a.queueNumber ASC, a.appointmentTime ASC, a.id DESC")
    List<Appointment> findCheckedInAppointments();

    @Query("SELECT a FROM Appointment a ORDER BY CASE WHEN a.status = com.example.case_study_2.entity.enums.AppointmentStatus.PENDING THEN 0 ELSE 1 END ASC, a.appointmentDate DESC, a.appointmentTime DESC, a.id DESC")
    List<Appointment> findAllForStaffManagement();

    List<Appointment> findAllByOrderByAppointmentDateAscAppointmentTimeAsc();
    List<Appointment> findAllByOrderByIdDesc();
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime AND a.status != 'CANCELLED'")
    long countBookedSlot(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate, @Param("appointmentTime") LocalTime appointmentTime);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.status != com.example.case_study_2.entity.enums.AppointmentStatus.CANCELLED AND ((:isMorning = true AND a.appointmentTime < :midday) OR (:isMorning = false AND a.appointmentTime >= :midday))")
    long countBookedAppointmentsForDoctorShift(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate, @Param("isMorning") boolean isMorning, @Param("midday") LocalTime midday);

    default long countBookedAppointmentsForDoctorAndShift(Long doctorId, LocalDate appointmentDate, Shift shift) {
        boolean isMorning = (shift == Shift.MORNING);
        return countBookedAppointmentsForDoctorShift(doctorId, appointmentDate, isMorning, LocalTime.of(12, 0));
    }

    @Query("SELECT MAX(a.queueNumber) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate")
    Integer findMaxQueueNumberForDoctorAndDate(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate);

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE Appointment a SET a.status = com.example.case_study_2.entity.enums.AppointmentStatus.CANCELLED, a.cancellationReason = 'Tự động hủy do quá hạn ngày khám' WHERE a.appointmentDate < :today AND a.status NOT IN (com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED, com.example.case_study_2.entity.enums.AppointmentStatus.CANCELLED)")
    int autoCancelOverdueAppointments(@Param("today") LocalDate today);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate < :today AND a.status NOT IN (com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED, com.example.case_study_2.entity.enums.AppointmentStatus.CANCELLED)")
    List<Appointment> findOverdueUncompletedAppointments(@Param("today") LocalDate today);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status NOT IN ('COMPLETED', 'CANCELLED')")
    long countUnfinishedAppointmentsByPatient(@Param("patientId") Long patientId);

    @Query("SELECT a.service.name, COUNT(a) FROM Appointment a GROUP BY a.service.name")
    List<Object[]> countAppointmentsByService();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE YEAR(a.appointmentDate) = :year AND MONTH(a.appointmentDate) = :month")
    long countAppointmentsInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED AND YEAR(a.appointmentDate) = :year AND MONTH(a.appointmentDate) = :month")
    long countCompletedAppointmentsInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = com.example.case_study_2.entity.enums.AppointmentStatus.CANCELLED AND YEAR(a.appointmentDate) = :year AND MONTH(a.appointmentDate) = :month")
    long countCancelledAppointmentsInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT a.service.name, COUNT(a) FROM Appointment a WHERE YEAR(a.appointmentDate) = :year AND MONTH(a.appointmentDate) = :month GROUP BY a.service.name ORDER BY a.service.name ASC")
    List<Object[]> countAppointmentsByServiceInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT a.doctor.user.fullName, COUNT(a) FROM Appointment a WHERE YEAR(a.appointmentDate) = :year AND MONTH(a.appointmentDate) = :month GROUP BY a.doctor.user.fullName ORDER BY a.doctor.user.fullName ASC")
    List<Object[]> countAppointmentsByDoctorInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT a.doctor.user.fullName, COUNT(a) FROM Appointment a WHERE a.status = com.example.case_study_2.entity.enums.AppointmentStatus.COMPLETED AND YEAR(a.appointmentDate) = :year AND MONTH(a.appointmentDate) = :month GROUP BY a.doctor.user.fullName ORDER BY a.doctor.user.fullName ASC")
    List<Object[]> countCompletedAppointmentsByDoctorInMonth(@Param("year") int year, @Param("month") int month);
}
