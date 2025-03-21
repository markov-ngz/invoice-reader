package invoiceReader;


import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String,String>, Void>{
    @Override
    public Void handleRequest(Map<String,String> event, Context context)
    {
      LambdaLogger logger = context.getLogger();
      logger.log("EVENT TYPE: " + event.getClass());
      return null;
    }
}