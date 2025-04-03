package analyzeDocument;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;

import analyzeDocument.dtos.AnalyzedInvoiceDTO;
import analyzeDocument.dtos.InvoiceDTO;

// import org.junit.jupiter.api.Test;

public class TestAnalyzedInvoiceSerialization {
    @Test
    public void testMissingRequiredEnvironmentVariables() {
        InvoiceDTO invoiceDTO = new InvoiceDTO() ; 
        invoiceDTO.setCustomerName("A");
        invoiceDTO.setId(1564);
        invoiceDTO.setSupplier("B");
        invoiceDTO.setSupplierAddress("4 r");
        invoiceDTO.setTotalAmount(12354.45);
        invoiceDTO.setInvoiceNumber("#46548");
        invoiceDTO.setInvoiceDate(null);

        List<InvoiceDTO> invoiceDTOs = new ArrayList<InvoiceDTO>() ; 
        invoiceDTOs.add(invoiceDTO) ; 

        AnalyzedInvoiceDTO analyzedInvoiceDTO = new AnalyzedInvoiceDTO("bucket", "objectKey", "null", 0, invoiceDTOs) ; 

        ObjectMapper objectMapper = new ObjectMapper() ; 

        assertDoesNotThrow(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(analyzedInvoiceDTO) ) ; 
        
    }
}
