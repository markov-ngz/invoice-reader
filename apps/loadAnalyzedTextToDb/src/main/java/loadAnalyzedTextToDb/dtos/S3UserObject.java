package loadAnalyzedTextToDb.dtos;

import java.util.List;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;



public class S3UserObject {

    private List<BlockDTO> blocks ;  // BLOCKDTO however DTO might no be appropriate
    private String bucketName; 
    private String objectKey;
    private int userId ;

    public S3UserObject(String bucketName, String objectKey, int userId){
        this.bucketName = bucketName ; 
        this.objectKey = objectKey ; 
        this.userId = userId ; 
    }
    

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static S3UserObject fromSQSMessage(SQSMessage sqsMessage)throws Exception{
        
        ObjectMapper objectMapper = new ObjectMapper() ; 
        
        String body = sqsMessage.getBody() ; 

        S3UserObject newS3UserFile =  objectMapper.readValue(body, S3UserObject.class) ; 

        return newS3UserFile ; 

    }

    public List<BlockDTO> getBlocks(){
        return this.blocks ; 
    }

    public void setBlocks(List<BlockDTO> blocks){
        this.blocks = blocks ; 
    }

}
