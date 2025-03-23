package invoiceReader;

import java.util.List;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.S3Object ; 


public class Handler implements RequestHandler<SQSEvent, String>{

    private TextractClient textractClient ; 

    LambdaLogger logger ; 

    SqsClient sqsClient ; 

    String queueUrl  = "https://sqs.eu-west-3.amazonaws.com/<project_id>/<name"; 

    ObjectMapper objectMapper = new ObjectMapper() ; 


    @Override
    public String handleRequest(SQSEvent event, Context context)
    {

      Region region = Region.EU_WEST_3 ; 

      this.logger = context.getLogger();
      
      logger.log("EVENT TYPE: " + event.getClass());


      this.textractClient = TextractClient.builder()
              .region(region)
              .build();

      this.sqsClient = SqsClient.create() ; 
      

      event.getRecords().forEach( m -> processSQSMessage(m)) ; 

      return  "return data do not matter" ;
      
    }

    /*
    * Process SQSMessage with a message body like S3UserObject
    * 1. Parse SQSMessageBody to  S3UserObjectwithText
    * 2. Extract Text from S3Object
    * 3. Write message to queue
    */
    private void processSQSMessage(SQSMessage sqsMessage){
        this.logger.log(sqsMessage.getBody());
        
        try {

          // 1. Parse Body 
          S3UserObjectwithText s3UserObject = (S3UserObjectwithText) S3UserObject.fromSQSMessage(sqsMessage) ;  


          // 2. Extract Text 
          List<Block> blocks = extractText(s3UserObject.getBucketName(), s3UserObject.getObjectKey()) ;

          s3UserObject.setBlocks(blocks);

          // 3. Write message 
          String messageBody = this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(s3UserObject) ; 

          sqsClient.sendMessage(SendMessageRequest.builder()
          .queueUrl(queueUrl)
          .messageBody(messageBody)
          .delaySeconds(10)
          .build());

        } catch (Exception e) {
          logger.log(e.getMessage(),LogLevel.ERROR) ;
        }

    }
    
    private List<Block> extractText(String bucketName, String objectKey){
      
      S3Object textractS3Object = S3Object.builder()
      .bucket(bucketName)
      .name(objectKey)
      .build();

      Document document = Document.builder()
      .s3Object(textractS3Object)
      .build();

      DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
      .document(document)
      .build();

      DetectDocumentTextResponse textResponse = this.textractClient.detectDocumentText(detectDocumentTextRequest);

      return textResponse.blocks() ; 
    }

}
