package loadAnalyzedTextToDb;

import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

public class Handler  implements RequestHandler<SQSEvent, String>{

    private static final Region DEFAULT_REGION = Region.EU_WEST_3;
    private  String QUEUE_URL ;

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        
        // 1. Extract Message content

        // 2. Write to Database with userId 
        
        return null ; 
    }
}
