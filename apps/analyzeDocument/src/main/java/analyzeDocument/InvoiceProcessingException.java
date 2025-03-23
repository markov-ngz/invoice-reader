package analyzeDocument;

// Custom exceptions for better error handling
class InvoiceProcessingException extends RuntimeException {
    public InvoiceProcessingException(String message) {
        super(message);
    }
    
    public InvoiceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
