package analyzeDocument;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;


import software.amazon.awssdk.eventnotifications.s3.model.S3;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification ;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import analyzeDocument.exceptions.*;
import analyzeDocument.dtos.AnalyzedInvoiceDTO;
import analyzeDocument.dtos.InvoiceDTO;
import analyzeDocument.services.S3Service;
import analyzeDocument.services.TextractService;
import analyzeDocument.services.mistral.MistralAnalyzeDocumentResponse;
import analyzeDocument.services.mistral.MistralChoice;
import analyzeDocument.services.mistral.MistralChoiceDTO;
import analyzeDocument.services.mistral.MistralFileSignedUrlResponse;
import analyzeDocument.services.mistral.MistralService;
import analyzeDocument.services.mistral.MistralUploadFileResponse;



public class Handler implements RequestHandler<SQSEvent, String> {

  private static final Region DEFAULT_REGION = Region.EU_WEST_3;
  private  String QUEUE_URL ;
  private static final int DEFAULT_SQS_DELAY_SECONDS = 10;

  private static final String mistralApiKeySecretName = "MISTRAL_API_KEY2" ; 
  
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  private SqsClient sqsClient;
  private LambdaLogger logger;
  private S3Client s3Client ; 
  private SecretsManagerClient secretClient ; 
  private MistralService mistralService ;

  @Override
  public String handleRequest(SQSEvent event, Context context) {

      //----------- Setup -------------

      // Logger instance 
      this.logger = context.getLogger();
      
      logger.log("Starting processing of SQSEvent with " + 
                 Optional.ofNullable(event.getRecords()).map(List::size).orElse(0) + 
                 " records", LogLevel.INFO);
      // Init clients 
      initializeClients();

      final String mistralApiKey = getSecret(mistralApiKeySecretName) ;
      
      // Instantiate services 
      S3Service s3Service = new S3Service(s3Client) ; 
      this.mistralService = new MistralService(mistralApiKey) ; 

      // Get destination queue 
      this.QUEUE_URL =  System.getenv("ANALYZED_DOCUMENT_QUEUE_URL") ; 


      logger.log("Analyze Documents will be written to queue : " + this.QUEUE_URL, LogLevel.INFO );

      logger.log("Begginning message process", LogLevel.INFO);

      //---------- Process Messages Event --------
      try {

        for(SQSMessage message : event.getRecords()){
            
            // Get S3Event notification 
            S3EventNotification s3EventNotification = S3EventNotification.fromJson(message.getBody()) ;  
            
            // if there is no records continue
            if(s3EventNotification.getRecords() == null ){
                logger.log("No S3EventNotification Records were found", LogLevel.WARN);
                continue ; 
            }

            // From the S3EventNotification extract the S3Objects concerned about the event
            List<S3> s3s = s3EventNotification.getRecords().stream().map( r -> r.getS3() ).collect(Collectors.toList()) ;

            logger.log(String.format("S3 Event Notification objects count : %s", s3s.size()),LogLevel.INFO);

            // For each object 
            for(S3 s3 : s3s){

                logger.log(String.format("Beggining process for file with bucket : %s and  name : %s",s3.getBucket().getName(), s3.getObject().getKey()), LogLevel.INFO);
                
                // 1. Extract userid from  metadata's file 
                Map<String,String> metadata = s3Service.fetchMetadata(s3.getBucket().getName(), s3.getObject().getKey()) ; 

                String invoiceIdMetadata = metadata.get("invoiceId") ; 

                logger.log(String.format("File : %s , has metadata invoiceId : %s", s3.getObject().getKey(), invoiceIdMetadata), LogLevel.INFO);

                int invoiceId = Integer.parseInt(invoiceIdMetadata); 

                // 2. Download file bytes 
                byte[] fileBytes = s3Service.downloadFileBytes(s3.getBucket().getName(), s3.getObject().getKey()) ; 

                logger.log(String.format("Sucessfully downloaded bytes for file %s , file bytes %s", s3.getObject().getKey(), fileBytes.length), LogLevel.INFO);

                // 3. Extract Invoice Information
                MistralAnalyzeDocumentResponse mistralAnalyzeDocumentResponse = mistralDocumentUnderstanding(fileBytes, s3.getObject().getKey());

                List<InvoiceDTO> invoiceDTOs = mistralAnalyzeDocumentResponse.getChoices().stream().map( m -> parseInvoiceFromMistralChoice(m)).collect(Collectors.toList()) ; 

                logger.log(String.format("Succesfully extracted information for %s invoice", invoiceDTOs), LogLevel.INFO) ; 

                // 4. Build the Payload 
                AnalyzedInvoiceDTO analyzedDocument = new AnalyzedInvoiceDTO(
                    s3.getBucket().getName(), 
                    s3.getObject().getKey(), 
                    s3.getObject().getUrlDecodedKey(),
                    invoiceId, 
                    invoiceDTOs
                );

                String messageBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(analyzedDocument) ; 

                logger.log(messageBody, LogLevel.INFO);                

                // 5. Write to queue 
                SendMessageRequest sendRequest = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(messageBody)
                .delaySeconds(DEFAULT_SQS_DELAY_SECONDS)
                .build();
        
                sqsClient.sendMessage(sendRequest);

            }
        }
      } catch (Exception e) {
          
        logger.log("Fatal error during event processing: " + e.getMessage(), LogLevel.ERROR);
          throw new InvoiceProcessingException("Failed to process invoice event", e);

      } finally {
          closeClients();
      }
        
      logger.log("Processing completed successfully", LogLevel.INFO);

      return "Success";

  }

  private void initializeClients() {
      
      logger.log("Initializing AWS clients", LogLevel.DEBUG);
      
      try {

        this.sqsClient = SqsClient.builder()
                  .region(DEFAULT_REGION)
                  .build();

        this.s3Client = S3Client
            .builder()
            .region(DEFAULT_REGION)
            .build();

        this.secretClient = SecretsManagerClient.builder()
            .region(DEFAULT_REGION)
            .build();
          
        logger.log("AWS clients initialized successfully", LogLevel.DEBUG);
      
    } catch (Exception e) {
          logger.log("Failed to initialize AWS clients: " + e.getMessage(), LogLevel.ERROR);
          throw new InvoiceProcessingException("Client initialization failed", e);
    }
  }

  private void closeClients() {
        logger.log("Closing AWS clients", LogLevel.DEBUG);
        
        try {
            
            if (sqsClient != null) {
                sqsClient.close();
            }
        } catch (Exception e) {
            logger.log("Error while closing clients: " + e.getMessage(), LogLevel.WARN);
        }
    }

    private String getSecret(String secretName){

        // Create a Secrets Manager client


        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
        getSecretValueResponse = this.secretClient.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
        // For a list of exceptions thrown, see
        // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        throw e;
        }

        return getSecretValueResponse.secretString();

        }
    }


    public MistralAnalyzeDocumentResponse mistralDocumentUnderstanding(byte[] fileBytes , String fileName ) throws Exception{

        ObjectMapper objectMapper = new ObjectMapper() ;

        String model =  "mistral-small-latest"; 
        String prompt = """
                Ne Répond que par le payload JSON suivant ( avec les bons datatypes ) en complétant les champs via les informations trouvées sur la facture donnée :
                ```
                {
                    "invoiceNumber": <string> , 
                    "invoiceDate" : <date> ,
                    "supplier" : <string> , 
                    "supplierAdress" : <string> , 
                    "customerName" : <string>,
                    "customerAdress" : <string> , 
                    "invoiceLines" :[
                        {
                            "description" : <string> , 
                            "quantity" : <int> , 
                            "unitPrice" : <double> , 
                            "tax" : <string> ,
                            "amount" : <double>
                        },
                        ...
                    ], 
                    "totalAmount" : <double> 
                }
                ``` 
                Si des informations ne peuvent pas être extraites, met une valeur nulle ou une liste vide .
                """;

        // 1. Upload file 
        MistralUploadFileResponse mistralUploadFileResponse =  mistralService.uploadFile("EDFFacture2.pdf", fileBytes) ; 
        
        
        String fileId = mistralUploadFileResponse.getId() ; 

        logger.log(fileId, LogLevel.INFO);

        try {

            // 2. Get File URL 
            MistralFileSignedUrlResponse mistralFileSignedUrlResponse = mistralService.getFileSignedURL(fileId) ; 
            
            
            // 3. Use this file to make the prompt and get response  
            String documentUrl = mistralFileSignedUrlResponse.getUrl() ;

            logger.log(documentUrl, LogLevel.INFO);
    
            MistralAnalyzeDocumentResponse mistralAnalyzedDocumentResponse =  mistralService.analyzeDocument(prompt, model, documentUrl) ;
            
            return mistralAnalyzedDocumentResponse ; 

        } catch (Exception e) {
            throw e ;
        } finally {
            // 4. Delete File 
            mistralService.deleteFile(fileId) ;
        }
        
    }

    public InvoiceDTO parseInvoiceFromMistralChoice(MistralChoiceDTO mistralChoice){

        String invoiceJson =  mistralChoice.getMessage().getContent() ; 
        try {
            return objectMapper.readValue(invoiceJson, InvoiceDTO.class) ; 
        } catch (Exception e) {
          e.printStackTrace();
          return null ; 
        }
        
    }

}
