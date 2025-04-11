package com.invoiceReader.InvoiceService.dtos ; 

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceDTO {
    private int id ;

    private String invoiceNumber;
    private Date invoiceDate;
    
    private String supplier ; 
    private String supplierAdress ;
    
    
    private String customerName;
    private String customerAdress;

    private List<InvoiceLineDTO> invoiceLines;
    
    private double totalAmount;
}
