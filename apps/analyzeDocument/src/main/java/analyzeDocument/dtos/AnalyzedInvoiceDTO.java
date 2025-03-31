package analyzeDocument.dtos;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

import software.amazon.awssdk.services.textract.model.Block;

import analyzeDocument.dtos.BlockDTO;


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
