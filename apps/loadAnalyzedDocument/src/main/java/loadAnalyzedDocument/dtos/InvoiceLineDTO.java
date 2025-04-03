package loadAnalyzedDocument.dtos;

import lombok.Data;

@Data
public class InvoiceLineDTO {
    private String description;
    private int quantity;
    private double unitPrice;
    private String tax  ;
    private double amount ;  
}
