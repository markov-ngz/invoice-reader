package invoiceReader;


import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String,String>, String>{
  
    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
      LambdaLogger logger = context.getLogger();
      logger.log("EVENT TYPE: " + event.getClass());

      String requestId = context.getAwsRequestId();

      return  "Hello, Lambda! Request ID: " + requestId; 
    }
}