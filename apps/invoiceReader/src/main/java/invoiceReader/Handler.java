package invoiceReader;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;


public class Handler implements RequestHandler<SQSEvent, String>{

    @Override
    public String handleRequest(SQSEvent event, Context context)
    {
      LambdaLogger logger = context.getLogger();
      
      logger.log("EVENT TYPE: " + event.getClass());

      event.getRecords().forEach( m -> logger.log(m.getBody())) ; 

      return  "Hello World" ; 
    }
}