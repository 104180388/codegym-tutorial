package com.example.case_study_2.repository;

import com.example.case_study_2.entity.Invoice;
import com.example.case_study_2.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceCode(String invoiceCode);
    Optional<Invoice> findByAppointmentId(Long appointmentId);

    @Query("SELECT i FROM Invoice i WHERE i.appointment.patient.id = :patientId ORDER BY i.createdAt DESC")
    List<Invoice> findByPatientId(@Param("patientId") Long patientId);

    List<Invoice> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = 'PAID' ORDER BY i.paymentTime DESC")
    List<Invoice> findAllPaidInvoices();

    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paymentTime BETWEEN :startDate AND :endDate ORDER BY i.paymentTime DESC")
    List<Invoice> findPaidInvoicesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    BigDecimal calculateTotalPaidRevenue();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paymentTime BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT YEAR(i.paymentTime), MONTH(i.paymentTime), SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' GROUP BY YEAR(i.paymentTime), MONTH(i.paymentTime) ORDER BY YEAR(i.paymentTime) ASC, MONTH(i.paymentTime) ASC")
    List<Object[]> getMonthlyRevenueTrends();

    @Query("SELECT i.paymentMethod, SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paymentTime BETWEEN :startDate AND :endDate GROUP BY i.paymentMethod")
    List<Object[]> getRevenueByPaymentMethodBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i.paymentMethod, SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' GROUP BY i.paymentMethod")
    List<Object[]> getAllRevenueByPaymentMethod();

    @Query("SELECT i.appointment.service.name, SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paymentTime BETWEEN :startDate AND :endDate GROUP BY i.appointment.service.name ORDER BY SUM(i.totalAmount) DESC")
    List<Object[]> getRevenueByServiceBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i.appointment.service.name, SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' GROUP BY i.appointment.service.name ORDER BY SUM(i.totalAmount) DESC")
    List<Object[]> getAllRevenueByService();

    @Query("SELECT i.appointment.service.name, SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND YEAR(i.paymentTime) = :year AND MONTH(i.paymentTime) = :month GROUP BY i.appointment.service.name ORDER BY i.appointment.service.name ASC")
    List<Object[]> getRevenueByServiceInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT i.appointment.doctor.user.fullName, SUM(i.totalAmount), COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND YEAR(i.paymentTime) = :year AND MONTH(i.paymentTime) = :month GROUP BY i.appointment.doctor.user.fullName ORDER BY i.appointment.doctor.user.fullName ASC")
    List<Object[]> getRevenueByDoctorInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND YEAR(i.paymentTime) = :year AND MONTH(i.paymentTime) = :month")
    BigDecimal getRevenueInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND YEAR(i.paymentTime) = :year AND MONTH(i.paymentTime) = :month")
    long countPaidInvoicesInMonth(@Param("year") int year, @Param("month") int month);
}
