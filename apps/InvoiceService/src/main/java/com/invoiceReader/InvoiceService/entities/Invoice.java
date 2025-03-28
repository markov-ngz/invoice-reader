package com.invoiceReader.InvoiceService.entities ; 

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id ;

    private String name ; 
}
