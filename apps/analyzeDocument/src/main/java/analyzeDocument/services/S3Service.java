package analyzeDocument.services;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.eventnotifications.s3.model.S3;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification ;


public class S3Service {

    private final S3Client s3Client ; 

    public S3Service(S3Client s3Client){
        this.s3Client = s3Client ;
    }

    public Map<String,String> fetchMetadata(String bucket ,String key){

        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

        return headObjectResponse.metadata() ; 
    }

    public List<S3> getS3sFromS3EventNotification(S3EventNotification s3EventNotification){
        
        return s3EventNotification.getRecords().stream().map( r -> r.getS3() ).collect(Collectors.toList()) ;
    }

        public byte[] downloadFileBytes(String bucket, String objectKey){

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(objectKey)
            .build() ; 
            
        ResponseBytes<GetObjectResponse> responseInputStream =  s3Client.getObjectAsBytes(getObjectRequest) ; 

        GetObjectResponse getObjectResponse = responseInputStream.response() ; 

        s3Client.close();

        return responseInputStream.asByteArray(); 
    }

    public void uploadFile(String bucket , byte[] content , String objectKey, String contentType, Map<String,String> metadata  ){
        
        PutObjectRequest putObjectRequest  = PutObjectRequest.builder()
            .bucket(bucket)
            .key(objectKey)
            .contentType(contentType)
            .metadata(metadata)
            .build() ; 

        RequestBody requestBody = RequestBody.fromBytes(content) ; 

        try {
            s3Client.putObject(putObjectRequest, requestBody) ; 
        } catch (Exception e) {
            throw e ;
        }finally{
            s3Client.close();
        }

    }
}
