package com.invoiceReader.InvoiceService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id ;
    private String description;
    private int quantity;
    private double unitPrice;
    private String tax  ;
    private double amount ; 

}
