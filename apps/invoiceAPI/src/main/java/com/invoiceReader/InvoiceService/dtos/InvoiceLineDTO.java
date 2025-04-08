package com.invoiceReader.InvoiceService.dtos ; 

import lombok.Data;

@Data
public class InvoiceLineDTO {
    private int id ; 
    private int invoiceId ;
    private String description;
    private int quantity;
    private double unitPrice;
    private String tax  ;
    private double amount ;  
 
}
