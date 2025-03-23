package invoiceReader;

import java.util.List;

import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.S3Object ; 

/*
* Service class to handle Textract operations
*/
public class TextractService {
    
    private final TextractClient textractClient;
    
    /*
     * Service class to handle Textract operations
     */
    public TextractService(TextractClient textractClient) {
        this.textractClient = textractClient;
    }
    
    public List<Block> extractText(String bucketName, String objectKey) {
        S3Object textractS3Object = S3Object.builder()
                .bucket(bucketName)
                .name(objectKey)
                .build();
        
        Document document = Document.builder()
                .s3Object(textractS3Object)
                .build();
        
        DetectDocumentTextRequest detectRequest = DetectDocumentTextRequest.builder()
                .document(document)
                .build();
        
        DetectDocumentTextResponse textResponse = textractClient.detectDocumentText(detectRequest);
        
        return textResponse.blocks();
    }
} 