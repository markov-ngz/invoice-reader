package com.invoiceReader.InvoiceService.dtos ; 

import lombok.Data;

@Data
public class InvoiceLineCreateDTO {
    private String description;
    private int quantity;
    private double unitPrice;
    private String tax  ;
    private double amount ;  
}
