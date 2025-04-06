package com.invoiceReader.InvoiceService.entities ; 

import java.sql.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id ;

    private String invoiceNumber;
    private Date invoiceDate;
    
    private String supplier ; // Of course Id would be better with real links and entities but this is done as a POC for OCR not to create an ERP from scratch 
    private String supplierAddress ;
    
    
    private String customerName;
    private String customerAddress;


    @OneToMany
    private List<InvoiceLine> invoiceLines;
    
    private double totalAmount;
}
