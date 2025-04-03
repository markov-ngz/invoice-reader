package loadAnalyzedDocument.dtos;

import java.util.List;

import lombok.Data;

@Data
public class AnalyzedInvoiceDTO {

    private String objectUrl ; 

    private String bucketName; 
    private String objectKey;
    
    private int invoiceId ;
    
    private List<InvoiceDTO> invoiceDTOs ; 

    public AnalyzedInvoiceDTO(String bucketName, String objectKey, String objectUrl , int invoiceId , List<InvoiceDTO> invoiceDTOs ){
        
        this.bucketName = bucketName ; 
        this.objectKey = objectKey ; 
        this.objectUrl = objectUrl ;
        this.invoiceId = invoiceId ;
        
        this.invoiceDTOs = invoiceDTOs ; 

    }

}
