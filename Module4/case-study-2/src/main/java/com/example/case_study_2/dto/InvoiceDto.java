package com.example.case_study_2.dto;

import com.example.case_study_2.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvoiceDto {

    @NotNull(message = "Lịch hẹn không hợp lệ")
    private Long appointmentId;

    @NotNull(message = "Phí dịch vụ không được để trống")
    private BigDecimal serviceFee;

    private BigDecimal surcharge = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    public InvoiceDto() {
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
