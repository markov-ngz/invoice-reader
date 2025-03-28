package loadAnalyzedTextToDb.dtos;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.textract.model.Block;


public class AnalyzedDocumentDTO {

    private String objectUrl ; 

    private String bucketName; 
    private String objectKey;
    
    private int userId ;
    
    private List<BlockDTO> blocks ; 

    public AnalyzedDocumentDTO(String bucketName, String objectKey, int userId , String objectUrl , List<Block> blocks ){
        this.bucketName = bucketName ; 
        this.objectKey = objectKey ; 
        this.objectUrl = objectUrl ;

        this.userId = userId ; 

        this.blocks = blocks.stream().map( b -> new BlockDTO(b)).collect(Collectors.toList()) ;
    }

}
