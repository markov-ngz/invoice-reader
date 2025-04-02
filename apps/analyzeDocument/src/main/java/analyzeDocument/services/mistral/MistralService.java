package analyzeDocument.services.mistral;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MistralService {
    
    private final String apiKey ; 

    private ObjectMapper objectMapper = new ObjectMapper() ; 

    private String baseUrl = "https://api.mistral.ai/v1" ; 

    private String uploadFileEndpoint = "/files" ; 

    private String getFileUrlEndpoint = "/files/%s/url" ; 

    private String deleteFileEndpoint = "/files/%s" ; 

    private String chatCompletionEndpoint = "/chat/completions" ; 

    public MistralService(String apiKey ){
        
        this.apiKey  = apiKey ;

    }


    public String deleteFile(String fileId) throws Exception {

        String formatEndpoint = String.format(deleteFileEndpoint, fileId) ; 

        String url = baseUrl + formatEndpoint ; 

        HttpDelete httpDelete = new HttpDelete(url) ; 

        httpDelete.setHeader("Authorization", "Bearer " + apiKey); // should be factorized 

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            return  httpClient.execute(httpDelete, response -> {
                System.out.println("Status: " + response.getCode() + " " + response.getReasonPhrase());
                HttpEntity responseEntity = response.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            });
        }      
    }

    public MistralAnalyzeDocumentResponse analyzeDocument( String prompt, String model , String documentUrl) throws Exception{

        String url = baseUrl + chatCompletionEndpoint ; 

        HttpPost httpPost = new HttpPost(url) ; 
        
        httpPost.setHeader("Authorization", "Bearer " + apiKey);

        MistralAnalyzeDocumentPayload mistralAnalyzeDocumentPayload = new MistralAnalyzeDocumentPayload(model, prompt, documentUrl) ; 

        String payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mistralAnalyzeDocumentPayload);

        System.out.println(payload);

        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON) ; 

        httpPost.setEntity(entity);

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            return  httpClient.execute(httpPost, response -> {
                System.out.println("Status: " + response.getCode() + " " + response.getReasonPhrase());
                HttpEntity responseEntity = response.getEntity();
                if(responseEntity != null){
                    String jsonResponse = EntityUtils.toString(responseEntity) ;
                    System.out.println(jsonResponse); 
                    return objectMapper.readValue(jsonResponse, MistralAnalyzeDocumentResponse.class) ; 
                }else{
                    return null ; 
                }
            });
        }
     }

    public MistralFileSignedUrlResponse getFileSignedURL(String fileId) throws Exception{

        String formatEndpoint = String.format(getFileUrlEndpoint, fileId) ; 

        String url = baseUrl + formatEndpoint ; 
        
        HttpGet httpGet = new HttpGet(url) ; 
        
        httpGet.setHeader("Authorization", "Bearer " + apiKey);

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){

            return httpClient.execute(httpGet, response -> {
                System.out.println("Status: " + response.getCode() + " " + response.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity) ; 
                System.out.println(jsonResponse);

                return entity != null ? objectMapper.readValue(jsonResponse, MistralFileSignedUrlResponse.class) : null;
            });
        }
    }

    public MistralUploadFileResponse uploadFile(String fileName , byte[] fileBytes)throws Exception{

        String url =baseUrl + uploadFileEndpoint ; 

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost uploadFile = new HttpPost(url);
            
            // 1. Set the Authorization header
            uploadFile.setHeader("Authorization", "Bearer " + apiKey);
            
            // 2. Create multipart entity
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", fileBytes, ContentType.APPLICATION_OCTET_STREAM, fileName);

            // 3. Add OCR purpose text body 
            builder.addTextBody("purpose", "ocr"); 
            
            // 4. Build and set the multipart entity to the "request"
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            
            // Execute the request
            MistralUploadFileResponse mistralUploadFileResponse =  httpClient.execute(uploadFile, response -> {
                System.out.println("Status: " + response.getCode() + " " + response.getReasonPhrase());

                HttpEntity entity = response.getEntity();

                String jsonResponse = EntityUtils.toString(entity); 

                System.out.println(jsonResponse);
                
                return entity != null ? objectMapper.readValue(jsonResponse, MistralUploadFileResponse.class) : null; 

            });

            return mistralUploadFileResponse ; 

    }
}
    
}