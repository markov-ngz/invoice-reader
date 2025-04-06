package loadAnalyzedDocument.dtos;

import lombok.Data;

@Data
public class AnalyzedInvoiceDTO {

    private String objectUrl ; 

    private String bucketName; 
    private String objectKey;
    
    private int invoiceId ;
    
    private InvoiceDTO invoiceDTO ; 

    public AnalyzedInvoiceDTO(String bucketName, String objectKey, String objectUrl , int invoiceId , InvoiceDTO invoiceDTOs ){
        
        this.bucketName = bucketName ; 
        this.objectKey = objectKey ; 
        this.objectUrl = objectUrl ;
        this.invoiceId = invoiceId ;
        
        this.invoiceDTO = invoiceDTOs ; 

    }

}
