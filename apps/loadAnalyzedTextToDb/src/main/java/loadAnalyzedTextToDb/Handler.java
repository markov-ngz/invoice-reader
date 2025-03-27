package loadAnalyzedTextToDb;

import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import loadAnalyzedTextToDb.config.DatabaseConfig;
import loadAnalyzedTextToDb.dtos.S3UserObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;


public class Handler  implements RequestHandler<SQSEvent, String>{

    private static final Region DEFAULT_REGION = Region.EU_WEST_3;
    private  String QUEUE_URL ;
    private LambdaLogger logger;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        
        this.logger = context.getLogger();

        DatabaseConfig databaseConfig = DatabaseConfig.load() ; 
        
        Connection connection = getConnection(databaseConfig) ; 

        
        for(SQSMessage message : event.getRecords()){

            try {
                processMessage(message) ; 
            } catch (Exception e) {
                logger.log(e.getMessage() , LogLevel.ERROR) ; 
            }
        
        }

        
        return null ; 
    }

    private Connection getConnection(DatabaseConfig dbConfig){
        try {
            return DriverManager.getConnection(
                dbConfig.getUrl() , // "jdbc:postgresql://localhost:5432/yourdatabase"  
                dbConfig.getUsername() ,  
                dbConfig.getPassword()
             );            
        } catch (SQLException e) {
            logger.log(e.getMessage(), LogLevel.ERROR);
            return null ; 
        }

    }

    private void processMessage(SQSMessage message) throws Exception{
        
        logger.log("Starting processing Message with Id :" + message.getMessageId(),LogLevel.INFO) ; 
        
        // 1. Extract Message content
        S3UserObject s3UserObject =  parseJson(message.getBody(), S3UserObject.class, objectMapper) ;

        // 2. Write Message to Database
        
    }

    public static <T> T parseJson(String jsonMessage, Class<T> clazz, ObjectMapper objectMapper) {
        
        
        try {
            return objectMapper.readValue(jsonMessage, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON message", e);
        }
    }
}
