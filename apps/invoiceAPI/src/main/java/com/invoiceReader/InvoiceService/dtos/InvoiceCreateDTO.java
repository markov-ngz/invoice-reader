package com.invoiceReader.InvoiceService.dtos ; 

import java.sql.Date;
import java.util.List;

import com.invoiceReader.InvoiceService.entities.InvoiceLine;

import lombok.Data;

@Data
public class InvoiceCreateDTO {

    private String invoiceNumber;
    private Date invoiceDate;
    
    private String supplier ; 
    private String supplierAdress ;
    
    
    private String customerName;
    private String customerAdress;

    private List<InvoiceLineCreateDTO> invoiceLines ; 
    
    private double totalAmount;
}
