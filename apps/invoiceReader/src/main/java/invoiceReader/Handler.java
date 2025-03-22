package invoiceReader;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import software.amazon.awssdk.eventnotifications.s3.model.S3;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification ;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.S3Object ; 


public class Handler implements RequestHandler<SQSEvent, String>{

    TextractClient textractClient ; 

    @Override
    public String handleRequest(SQSEvent event, Context context)
    {

      Region region = Region.EU_WEST_3 ; 

      LambdaLogger logger = context.getLogger();
      
      logger.log("EVENT TYPE: " + event.getClass());


      this.textractClient = TextractClient.builder()
              .region(region)
              .build();

      event.getRecords().forEach( m -> processSQSInvoiceEvent(m.getBody())) ; 

      return  "Hello World" ; 
    }

    private void processSQSInvoiceEvent(String sqsEventBody){
      S3EventNotification s3EventNotification = S3EventNotification.fromJson(sqsEventBody) ; 
      List<S3EventNotificationRecord> records = s3EventNotification.getRecords();
      records.forEach( record -> processS3InvoiceEvent(record) );
      
    }

    private void processS3InvoiceEvent(S3EventNotificationRecord s3EventNotificationRecord){

      S3 s3 = s3EventNotificationRecord.getS3() ; 

      S3Object textractS3Object = S3Object.builder()
      .bucket(s3.getBucket().getName())
      .name(s3.getObject().getKey())
      .build();

      Document document = Document.builder()
      .s3Object(textractS3Object)
      .build();

      // DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
      // .document(document)
      // .build();

      // DetectDocumentTextResponse textResponse = this.textractClient.an;

      
    }


}
// https://docs.aws.amazon.com/fr_fr/sdk-for-java/latest/developer-guide/examples-s3-event-notifications.html 
// use textract 
// https://docs.aws.amazon.com/fr_fr/sdk-for-java/latest/developer-guide/java_textract_code_examples.html 