package com.restroly.qrmenu.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_upi_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpiLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upi_link_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "upi_id", nullable = false, unique = true)
    private String upiId;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean defaultLink = false;

    @Column(nullable = false)
    @Builder.Default
    private Long transactions = 0L;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
