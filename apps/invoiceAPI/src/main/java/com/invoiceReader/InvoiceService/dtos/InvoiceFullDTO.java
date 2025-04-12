package com.invoiceReader.InvoiceService.dtos;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceFullDTO {
    
    private int id ;

    private String invoiceNumber;
    private Date invoiceDate;
    
    private String supplier ; 
    private String supplierAdress ;
    
    
    private String customerName;
    private String customerAdress;
    
    private double totalAmount;

    private List<InvoiceLineDTO> invoiceLines  ;
}
