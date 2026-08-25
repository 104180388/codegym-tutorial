package com.codegym.casestudy.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AppointmentServicesId implements Serializable {

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "service_id")
    private Long serviceId;
}
