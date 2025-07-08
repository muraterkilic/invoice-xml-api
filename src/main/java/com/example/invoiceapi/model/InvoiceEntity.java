package com.example.invoiceapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "invoices")
@Data
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nip;

    @Column(nullable = false)
    private String p1;

    @Column(nullable = false)
    private String p2;
}
