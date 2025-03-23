package analyzeDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;

import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.textract.model.TextractException;

public class Handler implements RequestHandler<SQSEvent, String> {

  private static final Region DEFAULT_REGION = Region.EU_WEST_3;
  private static final String QUEUE_URL = "https://sqs.eu-west-3.amazonaws.com/<project_id>/<name>";
  private static final int DEFAULT_SQS_DELAY_SECONDS = 10;
  
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  private TextractClient textractClient;
  private SqsClient sqsClient;
  private LambdaLogger logger;
  private S3Client s3Client ; 

  private TextractService textractService ; 

  @Override
  public String handleRequest(SQSEvent event, Context context) {
      this.logger = context.getLogger();
      logger.log("Starting processing of SQSEvent with " + 
                 Optional.ofNullable(event.getRecords()).map(List::size).orElse(0) + 
                 " records", LogLevel.INFO);
      
      initializeClients();

      initializeServices() ; 
      
      try {
          processMessages(event);
          logger.log("Processing completed successfully", LogLevel.INFO);
          return "Success";
      } catch (Exception e) {
          logger.log("Fatal error during event processing: " + e.getMessage(), LogLevel.ERROR);
          throw new InvoiceProcessingException("Failed to process invoice event", e);
      } finally {
          closeClients();
      }
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

  private void initializeServices(){
    logger.log("Initializing AWS services", LogLevel.DEBUG);
      
    try {
        this.textractService = new TextractService(textractClient) ;
        
        logger.log("AWS services initialized successfully", LogLevel.DEBUG);
    } catch (Exception e) {
        logger.log("Failed to initialize AWS services: " + e.getMessage(), LogLevel.ERROR);
        throw new InvoiceProcessingException("Service initialization failed", e);
    }

  }
  


  private void processMessages(SQSEvent event) {
      if (event == null || event.getRecords() == null || event.getRecords().isEmpty()) {
          logger.log("No records to process", LogLevel.WARN);
          return;
      }
      
      logger.log("Processing " + event.getRecords().size() + " messages", LogLevel.INFO);
      
      for (SQSMessage message : event.getRecords()) {
          try {
              processSingleMessage(message);
          } catch (Exception e) {
              logger.log("Error processing message: " + e.getMessage() + ". Continuing with next message.", LogLevel.ERROR);
              // Continue processing other messages even if one fails
          }
      }
  }
  
  private void processSingleMessage(SQSMessage message) {
      if (message == null) {
          logger.log("Null message received, skipping", LogLevel.WARN);
          return;
      }
      
      String messageId = message.getMessageId();
      logger.log("Processing message with ID: " + messageId, LogLevel.INFO);
      
      try {
          // Parse the message body
          List<S3UserObject> s3UserObjects = parseMessageBody(message);

          

          for (S3UserObject s3UserObject : s3UserObjects) {
            processSingleS3UserObject(s3UserObject);
          }
          
          
          logger.log("Successfully processed message: " + messageId, LogLevel.INFO);
      } catch (Exception e) {
          logger.log("Failed to process message " + messageId + ": " + e.getMessage(), LogLevel.ERROR);
          throw new MessageProcessingException("Error processing message: " + messageId, e);
      }
  }
  
  private List<S3UserObject> parseMessageBody(SQSMessage message) {

      logger.log("Parsing message body" , LogLevel.DEBUG);
      
      try {
          String messageBody = message.getBody();
          if (messageBody == null || messageBody.isEmpty()) {
              throw new MessageProcessingException("Empty message body");
          }
          
          S3EventNotification s3EventNotification = S3EventNotification.fromJson(messageBody) ; 

          List<S3EventNotificationRecord> records = s3EventNotification.getRecords();


          List<S3UserObject> s3UserObjects = s3UserObjectsfromS3EventNotificationRecords(records, this.s3Client);

          if (s3UserObjects.isEmpty()){
            throw new MessageProcessingException("Failed to parse S3UserObject from message");
          }
         
          return s3UserObjects ; 

      } catch (Exception e) {
          logger.log("Error parsing message body: " + e.getMessage(), LogLevel.ERROR);
          throw new MessageProcessingException("Failed to parse message body", e);
      }
  }
  private List<S3UserObject> s3UserObjectsfromS3EventNotificationRecords(List<S3EventNotificationRecord> records, S3Client s3Client) throws S3ObjectAttributeNotFoundException{
        
        List<S3UserObject> s3UserObjects = new ArrayList<S3UserObject>()  ; 

        for (S3EventNotificationRecord record : records) {
            
            S3Entity s3 = record.getS3() ;

            S3ObjectEntity object = s3.getObject() ; 

            GetObjectAttributesRequest getObjectAttributesRequest = GetObjectAttributesRequest.builder().bucket(s3.getBucket().getName()).key(object.getKey()).build() ;  
            
            GetObjectAttributesResponse getObjectAttributesResponse =  s3Client.getObjectAttributes(getObjectAttributesRequest) ; 

            Integer userId =getObjectAttributesResponse.getValueForField("USER_ID", Integer.class).orElse(null ) ; 

            if(userId != null){
                S3UserObject s3UserObject = new S3UserObject(s3.getBucket().getName(),object.getKey(),userId.intValue()) ; 

                s3UserObjects.add(s3UserObject) ; 
            }
        }

        return s3UserObjects ; 
}

  private void processSingleS3UserObject(S3UserObject s3UserObject){

    // Extract text from S3 object
    List<Block> textBlocks = extractTextFromDocument(s3UserObject);
    
    // Update the S3 object with extracted text
    s3UserObject.setBlocks(textBlocks);
    
    // Send to output queue
    sendToOutputQueue(s3UserObject);

    }

  private List<Block> extractTextFromDocument(S3UserObject s3UserObject) {
    String bucketName = s3UserObject.getBucketName();
    String objectKey = s3UserObject.getObjectKey();
    
    logger.log("Extracting text from document in bucket: " + bucketName + ", key: " + objectKey, LogLevel.INFO);
    
    try {
        return new TextractService(textractClient).extractText(bucketName, objectKey);
    } catch (TextractException e) {
        logger.log("Textract service error: " + e.getMessage(), LogLevel.ERROR);
        throw new TextExtractionException("Failed to extract text using Textract", e);
    } catch (Exception e) {
        logger.log("Unexpected error during text extraction: " + e.getMessage(), LogLevel.ERROR);
        throw new TextExtractionException("Unexpected error during text extraction", e);
    }
}

  
  private void sendToOutputQueue(S3UserObject s3UserObject) {
      logger.log("Sending processed document to output queue", LogLevel.INFO);
      
      try {
          String messageBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(s3UserObject);
          
          SendMessageRequest sendRequest = SendMessageRequest.builder()
                  .queueUrl(QUEUE_URL)
                  .messageBody(messageBody)
                  .delaySeconds(DEFAULT_SQS_DELAY_SECONDS)
                  .build();
          
          sqsClient.sendMessage(sendRequest);
          
          logger.log("Successfully sent message to output queue", LogLevel.INFO);
      } catch (SqsException e) {
          logger.log("SQS service error: " + e.getMessage(), LogLevel.ERROR);
          throw new MessageSendException("Failed to send message to SQS", e);
      } catch (Exception e) {
          logger.log("Error sending message to output queue: " + e.getMessage(), LogLevel.ERROR);
          throw new MessageSendException("Error sending message to output queue", e);
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
}
