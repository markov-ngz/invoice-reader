package invoiceReader;


import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification ;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;
import software.amazon.awssdk.eventnotifications.s3.model.S3Object; 


public class Handler implements RequestHandler<SQSEvent, String>{

    @Override
    public String handleRequest(SQSEvent event, Context context)
    {
      LambdaLogger logger = context.getLogger();
      
      logger.log("EVENT TYPE: " + event.getClass());

      event.getRecords().forEach( m -> processSQSInvoiceEvent(m.getBody())) ; 

      return  "Hello World" ; 
    }

    private void processSQSInvoiceEvent(String sqsEventBody){
      S3EventNotification s3EventNotification = S3EventNotification.fromJson(sqsEventBody) ; 
      List<S3EventNotificationRecord> records = s3EventNotification.getRecords();
      records.forEach( record -> processS3InvoiceEvent(record) );
      
    }

    private void processS3InvoiceEvent(S3EventNotificationRecord s3EventNotificationRecord){

      S3Object S3Object = s3EventNotificationRecord.getS3().getObject() ; 
    
    }


}

// 1. Extract file name from body 
// https://docs.aws.amazon.com/fr_fr/sdk-for-java/latest/developer-guide/examples-s3-event-notifications.html 

//import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification
// import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord
// import software.amazon.awssdk.services.sqs.model.Message; 

// public class S3EventNotificationExample {
//     ...
    
//     void receiveMessage(Message message) {
//        // Message received from SQSClient.
//        String sqsEventBody = message.body();
//        S3EventNotification s3EventNotification = S3EventNotification.fromJson(sqsEventBody);
//         // Use getters on the record to access individual attributes.
//         String awsRegion = record.getAwsRegion();
//         String eventName = record.getEventName();
//         String eventSource = record.getEventSource();                                                                                                   
//     }
// }
// 


// use textract 
// https://docs.aws.amazon.com/fr_fr/sdk-for-java/latest/developer-guide/java_textract_code_examples.html 