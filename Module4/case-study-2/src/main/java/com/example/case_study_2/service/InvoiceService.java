package com.example.case_study_2.service;

import com.example.case_study_2.dto.InvoiceDto;
import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.Invoice;
import com.example.case_study_2.entity.Staff;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.PaymentStatus;
import com.example.case_study_2.repository.AppointmentRepository;
import com.example.case_study_2.repository.InvoiceRepository;
import com.example.case_study_2.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    public List<Invoice> getInvoicesByPatientId(Long patientId) {
        return invoiceRepository.findByPatientId(patientId);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getUnpaidInvoices() {
        return invoiceRepository.findByPaymentStatus(PaymentStatus.UNPAID);
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));
    }

    public Optional<Invoice> getInvoiceByAppointmentId(Long appointmentId) {
        return invoiceRepository.findByAppointmentId(appointmentId);
    }

    @Transactional
    public Invoice createOrGetInvoiceForAppointment(Long appointmentId, Long staffUserId) {
        Optional<Invoice> existing = invoiceRepository.findByAppointmentId(appointmentId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn"));

        Staff staff = staffRepository.findByUserId(staffUserId)
                .orElse(staffRepository.findAll().stream().findFirst().orElseThrow(() -> new IllegalStateException("Chưa có nhân viên lễ tân trong hệ thống")));

        Invoice invoice = new Invoice();
        invoice.setInvoiceCode("INV-" + System.currentTimeMillis() % 1000000);
        invoice.setAppointment(app);
        invoice.setStaff(staff);
        invoice.setServiceFee(app.getService().getPrice());
        invoice.setSurcharge(BigDecimal.ZERO);
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setTotalAmount(app.getService().getPrice());
        invoice.setPaymentStatus(PaymentStatus.UNPAID);

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice processPayment(InvoiceDto dto, Long staffUserId) {
        Invoice invoice = createOrGetInvoiceForAppointment(dto.getAppointmentId(), staffUserId);

        BigDecimal fee = dto.getServiceFee() != null ? dto.getServiceFee() : invoice.getServiceFee();
        BigDecimal surcharge = dto.getSurcharge() != null ? dto.getSurcharge() : BigDecimal.ZERO;
        BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;

        BigDecimal total = fee.add(surcharge).subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        invoice.setServiceFee(fee);
        invoice.setSurcharge(surcharge);
        invoice.setDiscount(discount);
        invoice.setTotalAmount(total);
        if (dto.getPaymentMethod() != null) {
            invoice.setPaymentMethod(dto.getPaymentMethod());
        }
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaymentTime(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);

        // Update appointment status to COMPLETED
        Appointment app = invoice.getAppointment();
        app.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(app);

        return saved;
    }
}
