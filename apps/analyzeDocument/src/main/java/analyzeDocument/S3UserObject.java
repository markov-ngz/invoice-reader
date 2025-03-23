package analyzeDocument;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3ObjectEntity;

import software.amazon.awssdk.eventnotifications.s3.model.S3;
import software.amazon.awssdk.eventnotifications.s3.model.S3Object;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;

public class S3UserObject {
    
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

    public static List<S3UserObject> fromS3EventNotificationRecords(List<S3EventNotificationRecord> records, S3Client s3Client) throws S3ObjectAttributeNotFoundException{
        
        List<S3UserObject> s3UserObjects = new ArrayList<S3UserObject>()  ; 

        for (S3EventNotificationRecord record : records) {
            
            S3Entity s3 = record.getS3() ;

            S3ObjectEntity object = s3.getObject() ; 

            GetObjectAttributesRequest getObjectAttributesRequest = GetObjectAttributesRequest.builder().bucket(s3.getBucket().getName()).key(object.getKey()).build() ;  
            
            GetObjectAttributesResponse getObjectAttributesResponse =  s3Client.getObjectAttributes(getObjectAttributesRequest) ; 

            Integer userId =getObjectAttributesResponse.getValueForField("USER_ID", Integer.class).orElseThrow(() -> new S3ObjectAttributeNotFoundException("Attribute not found")) ; 

            S3UserObject s3UserObject = new S3UserObject(s3.getBucket().getName(),object.getKey(),userId.intValue()) ; 

            s3UserObjects.add(s3UserObject) ; 
        }

        return s3UserObjects ; 
    }
}
