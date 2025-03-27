package loadAnalyzedTextToDb;

import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import analyzeDocument.S3UserObject;

public class Handler  implements RequestHandler<SQSEvent, String>{

    private static final Region DEFAULT_REGION = Region.EU_WEST_3;
    private  String QUEUE_URL ;
    private LambdaLogger logger;

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        
        this.logger = context.getLogger();

        // 1. Extract Message content
        for(SQSMessage message : event.getRecords()){

            try {
                processMessage(message) ; 
            } catch (Exception e) {
                logger.log(e.getMessage() , LogLevel.ERROR) ; 
            }
        
        }

        // 2. Write to Database with userId 
        
        return null ; 
    }

    private void processMessage(SQSMessage message) throws Exception{
        
        logger.log("Starting processing Message with Id :" + message.getMessageId(),LogLevel.INFO) ; 
        
        String body = message.getBody() ; 
        
    }
}
