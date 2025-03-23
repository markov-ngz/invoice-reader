package com.markovngz;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;


import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class Handler implements RequestHandler<SQSEvent, Void>{

    @Override
    public Void handleRequest(SQSEvent event, Context context)
    {

      LambdaLogger logger = context.getLogger();

      event.getRecords().forEach( m -> m.getAttributes().forEach((key,value) -> logger.log("Attribute : ( "+ key + ", " + value + " )"))) ; // log attributes with key value 

      SqsClient sqsClient = SqsClient.create() ; 

      event.getRecords().forEach( m -> sendMessage(m.getAttributes().get("ResponseQueueUrl"), sqsClient)) ;

      // write record to queue extracted from attributes ? 
      return null ;
    }

    private void sendMessage(String queueUrl, SqsClient sqsClient){
      if(queueUrl.length()>5 ){
        sqsClient.sendMessage(SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody("42 is and will always be the response to everything whether you like it or not")
        .delaySeconds(10)
        .build());
        }
    }

}