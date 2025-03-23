package analyzeDocument;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3ObjectEntity;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.textract.model.Block;

public class S3UserObject {

    private List<Block> blocks ; 
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

    public List<Block> getBlocks(){
        return this.blocks ; 
    }

    public void setBlocks(List<Block> blocks){
        this.blocks = blocks ; 
    }

}
