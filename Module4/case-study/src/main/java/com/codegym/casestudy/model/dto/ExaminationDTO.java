package com.codegym.casestudy.model.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExaminationDTO {
    private Long appointmentId;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reexaminationDate;

    @Builder.Default
    private List<Long> addServiceIds = new ArrayList<>();

    @Builder.Default
    private List<Long> medicineIds = new ArrayList<>();

    @Builder.Default
    private List<Integer> quantities = new ArrayList<>();

    @Builder.Default
    private List<String> dosages = new ArrayList<>();

    private String instructions;
}
