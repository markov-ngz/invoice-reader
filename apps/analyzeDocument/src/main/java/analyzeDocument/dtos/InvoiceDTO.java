package analyzeDocument.dtos;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceDTO {
    private int id ;

    private String invoiceNumber;
    private Date invoiceDate;
    
    private String supplier ; // Of course Id would be better with real links and entities but this is done as a POC for OCR not to create an ERP from scratch 
    private String supplierAddress ;
    
    
    private String customerName;
    private String customerAddress;

    private List<InvoiceLineDTO> items;
    
    private double totalAmount;
}
