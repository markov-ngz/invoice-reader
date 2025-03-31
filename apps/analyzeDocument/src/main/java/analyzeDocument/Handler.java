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


import analyzeDocument.exceptions.*;
import analyzeDocument.dtos.AnalyzedInvoiceDTO;
import analyzeDocument.dtos.InvoiceDTO;
import analyzeDocument.services.S3Service;
import analyzeDocument.services.TextractService;
import analyzeDocument.services.mistral.MistralAnalyzeDocumentResponse;
import analyzeDocument.services.mistral.MistralChoice;
import analyzeDocument.services.mistral.MistralFileSignedUrlResponse;
import analyzeDocument.services.mistral.MistralService;
import analyzeDocument.services.mistral.MistralUploadFileResponse;

public class Handler implements RequestHandler<SQSEvent, String> {

  private static final Region DEFAULT_REGION = Region.EU_WEST_3;
  private  String QUEUE_URL ;
  private static final int DEFAULT_SQS_DELAY_SECONDS = 10;
  private static final String mistralApiKeyEnvVariable = "MISTRAL_API_KEY" ; 
  
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  private TextractClient textractClient;
  private SqsClient sqsClient;
  private LambdaLogger logger;
  private S3Client s3Client ; 
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
      
      // Instantiate services 
      S3Service s3Service = new S3Service(s3Client) ; 
      TextractService textractService =  new TextractService(textractClient) ; 
      this.mistralService = new MistralService(mistralApiKeyEnvVariable) ; 

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

            // For each object 
            for(S3 s3 : s3s){
                
                // 1. Extract userid from  metadata's file 
                Map<String,String> metadata = s3Service.fetchMetadata(s3.getBucket().getName(), s3.getObject().getKey()) ; 

                String userIdMetadata = metadata.get("userid") ; 

                int userId = Integer.parseInt(userIdMetadata); 

                // 2. Download file bytes 
                byte[] fileBytes = s3Service.downloadFileBytes(s3.getBucket().getName(), s3.getObject().getKey()) ; 

                // 3. Call Textract API 
                // List<Block> blocks= textractService.extractText(s3.getBucket().getName(), s3.getObject().getKey()) ; 
                MistralAnalyzeDocumentResponse mistralAnalyzeDocumentResponse = mistralDocumentUnderstanding(fileBytes, s3.getObject().getKey());

                List<InvoiceDTO> mistralChoices = mistralAnalyzeDocumentResponse.getChoices().stream().map( m -> parseInvoiceFromMistralResponse(m)) ; 

                // 4. Build the Payload 
                AnalyzedInvoiceDTO analyzedDocument = new AnalyzedInvoiceDTO(
                    s3.getBucket().getName(), 
                    s3.getObject().getKey(), 
                    userId, 
                    s3.getObject().getUrlDecodedKey(), 
                   );

                String messageBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(analyzedDocument) ; 

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
        
        this.textractClient = TextractClient.builder()
                  .region(DEFAULT_REGION)
                  .build();
          
        this.sqsClient = SqsClient.builder()
                  .region(DEFAULT_REGION)
                  .build();

        this.s3Client = S3Client
            .builder()
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
            if (textractClient != null) {
                textractClient.close();
            }
            
            if (sqsClient != null) {
                sqsClient.close();
            }
        } catch (Exception e) {
            logger.log("Error while closing clients: " + e.getMessage(), LogLevel.WARN);
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



        try {

            // 2. Get File URL 
            MistralFileSignedUrlResponse mistralFileSignedUrlResponse = mistralService.getFileSignedURL(fileId) ; 
            
            
            // 3. Use this file to make the prompt and get response  
            String documentUrl = mistralFileSignedUrlResponse.getUrl() ;

            System.out.println(documentUrl)  ;
    
            MistralAnalyzeDocumentResponse mistralAnalyzedDocumentResponse =  mistralService.analyzeDocument(prompt, model, documentUrl) ;
            
            return mistralAnalyzedDocumentResponse ; 

        } catch (Exception e) {
            throw e ;
        } finally {
            // 4. Delete File 
            mistralService.deleteFile(fileId) ;
        }
        
    }

    public InvoiceDTO parseInvoiceFromMistralChoice(MistralChoice mistralChoice) throws Exception{

        String invoiceJson =  mistralChoice.getMessage().getContent().get(0).get("text") ; 
        try {
            return objectMapper.readValue(invoiceJson, InvoiceDTO.class) ; 
        } catch (Exception e) {
          e.printStackTrace();
          return null ; 
        }
        
    }

}
