package analyzeDocument;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


import org.junit.jupiter.api.Test;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;

import analyzeDocument.dtos.AnalyzedInvoiceDTO;
import analyzeDocument.dtos.InvoiceDTO;

// import org.junit.jupiter.api.Test;

public class TestAnalyzedInvoiceSerialization {
    @Test
    public void testInvoiceDTOdeserialization() {
        InvoiceDTO invoiceDTO = new InvoiceDTO() ; 
        invoiceDTO.setCustomerName("A");
        invoiceDTO.setCustomerAdress("5 r");
        invoiceDTO.setId(1564);
        invoiceDTO.setSupplier("B");
        invoiceDTO.setSupplierAdress("4 r");
        invoiceDTO.setTotalAmount(12354.45);
        invoiceDTO.setInvoiceNumber("#46548");
        invoiceDTO.setInvoiceDate(null);

        AnalyzedInvoiceDTO analyzedInvoiceDTO = new AnalyzedInvoiceDTO("bucket", "objectKey", "null", 0, invoiceDTO) ; 

        ObjectMapper objectMapper = new ObjectMapper() ; 

        assertDoesNotThrow(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(analyzedInvoiceDTO) ) ; 
        
    }
}
