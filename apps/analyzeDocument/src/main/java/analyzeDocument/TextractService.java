package analyzeDocument;

import java.util.List;

import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Block;
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
        
        AnalyzeDocumentRequest detectRequest = AnalyzeDocumentRequest.builder()
        .document(document)
        .build();
        
        AnalyzeDocumentResponse textResponse = textractClient.analyzeDocument(detectRequest);
        
        return textResponse.blocks();
    }
} 