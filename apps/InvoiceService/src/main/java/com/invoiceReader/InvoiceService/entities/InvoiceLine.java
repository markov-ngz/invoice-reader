package com.invoiceReader.InvoiceService.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class InvoiceLine {

    private String description;
    private int quantity;
    private double unitPrice;
    private String tax  ;
    private double amount ; 

}
