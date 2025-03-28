package analyzeDocument.exceptions;

// Custom exceptions for better error handling
public class InvoiceProcessingException extends RuntimeException {
    public InvoiceProcessingException(String message) {
        super(message);
    }
    
    public InvoiceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
