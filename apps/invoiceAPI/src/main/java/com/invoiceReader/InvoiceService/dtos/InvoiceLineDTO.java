package com.invoiceReader.InvoiceService.dtos ; 

import lombok.Data;

@Data
public class InvoiceLineDTO {
    private int id ; 
    private String description;
    private int quantity;
    private double unitPrice;
    private String tax  ;
    private double amount ;  
}
