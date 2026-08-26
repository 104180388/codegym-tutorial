package com.codegym.casestudy.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "active_ingredient")
    private String activeIngredient;

    @Column(nullable = false)
    private String unit; // Viên, Lọ, Vỉ, Chai, Hộp...

    @Column(nullable = false)
    private Double price;

    @Column(name = "usage_instruction", columnDefinition = "TEXT")
    private String usageInstruction;

    @Builder.Default
    private boolean active = true;
}
