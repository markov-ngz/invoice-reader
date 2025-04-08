package com.invoiceReader.InvoiceService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @ManyToOne
    @JoinColumn(name="invoice_id")
    private Invoice invoice ; 


    @JsonIgnore
    public Invoice getInvoice() {
        return invoice;
    }
    
    public int getInvoiceId() {
        return invoice != null ? invoice.getId() : null;
    }

}
