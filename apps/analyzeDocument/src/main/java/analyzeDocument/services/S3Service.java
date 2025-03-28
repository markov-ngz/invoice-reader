package analyzeDocument.services;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

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
}
