package com.codegym.casestudy.model.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {
    private Long specialtyId;
    private Long doctorId;
    private Long scheduleId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appointmentDate;
    private String appointmentTime;

    private String notes;
    private List<Long> serviceIds; // Danh sách ID các dịch vụ cận lâm sàng chọn trước
}
