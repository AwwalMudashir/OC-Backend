package com.project.pc_backend.model;

import com.project.pc_backend.dto.DonationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;

    private BigDecimal amount;

    private String reference;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

