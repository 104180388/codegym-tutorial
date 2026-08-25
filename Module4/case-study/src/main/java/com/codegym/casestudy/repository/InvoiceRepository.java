package com.codegym.casestudy.repository;

import com.codegym.casestudy.model.entity.Invoice;
import com.codegym.casestudy.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceCode(String invoiceCode);
    Optional<Invoice> findByAppointmentId(Long appointmentId);
    List<Invoice> findByPatientId(Long patientId);
    List<Invoice> findByPaymentStatus(PaymentStatus paymentStatus);
}
