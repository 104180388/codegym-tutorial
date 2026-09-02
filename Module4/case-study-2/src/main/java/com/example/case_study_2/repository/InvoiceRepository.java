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

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    BigDecimal calculateTotalPaidRevenue();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paymentTime BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
