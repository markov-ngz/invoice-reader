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
import loadAnalyzedTextToDb.services.S3UserObjectService;

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

        // 0. Setup 

        // 0.1 Logger
        this.logger = context.getLogger();

        // 0.2.1 Database Config 
        DatabaseConfig databaseConfig = DatabaseConfig.load() ; 
        
        // 0.2.2 Database Conn from Database Config
        Connection connection = getConnection(databaseConfig) ; 

        // 0.2.3 Instantiate service from Connection 
        S3UserObjectService s3UserObjectService = new S3UserObjectService(connection) ; 

        
        for(SQSMessage message : event.getRecords()){

            logger.log("Starting processing Message with Id :" + message.getMessageId(),LogLevel.INFO) ; 
            
            try {    
        
                // 1. Extract Message content
                S3UserObject s3UserObject =  parseJson(message.getBody(), S3UserObject.class, objectMapper) ;
        
                logger.log("Succesfully parsed Message with Id :" + message.getMessageId(),LogLevel.INFO) ; 
        
                // 2. Write Message to Database
                s3UserObjectService.processAndStoreS3UserObject(s3UserObject);
        
                logger.log("Successfully processed message with Id" + message.getMessageId(),LogLevel.INFO) ; 

            } catch (Exception e) {
                logger.log("Failed processed message with Id" + message.getMessageId() + " with err " + e.getMessage() ,LogLevel.ERROR) ; 
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

    public static <T> T parseJson(String jsonMessage, Class<T> clazz, ObjectMapper objectMapper) {
        
        try {
            return objectMapper.readValue(jsonMessage, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON message", e);
        }
    }
}
