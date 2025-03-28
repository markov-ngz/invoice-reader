package com.invoiceReader.InvoiceService.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileService {
    

    private final String bucket = "invoice-reader-bucket"; 

    private final String defaultPath = "invoices/" ; 

    private static final Region DEFAULT_REGION = Region.EU_WEST_3;

    public FileService(){}

    public void uploadFile(byte[] content , String fileName, String contentType, Map<String,String> metadata  ){

        S3Client s3Client = S3Client
            .builder()
            .region(DEFAULT_REGION)
            .build();
        
        PutObjectRequest putObjectRequest  = PutObjectRequest.builder()
            .bucket(bucket)
            .key(defaultPath + fileName)
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
