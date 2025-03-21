package invoiceReader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Object, String>{

    @Override
    public String handleRequest(Object event, Context context)
    {
      LambdaLogger logger = context.getLogger();

      logger.log("EVENT TYPE: " + event.getClass());

      return  "Hello, Lambda!"; 
    }
}