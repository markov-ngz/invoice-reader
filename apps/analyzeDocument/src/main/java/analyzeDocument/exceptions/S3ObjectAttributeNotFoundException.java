package analyzeDocument.exceptions;

public class S3ObjectAttributeNotFoundException extends Exception {
    public S3ObjectAttributeNotFoundException(String message) {
        super(message);
    }
    
    public S3ObjectAttributeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
