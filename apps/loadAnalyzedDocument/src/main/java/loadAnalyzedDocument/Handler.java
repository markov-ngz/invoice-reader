package loadAnalyzedDocument;

import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import loadAnalyzedDocument.dtos.AnalyzedInvoiceDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;

public class Handler implements RequestHandler<Object, String>{


    private Logger logger ;
  
    @Override
    public String handleRequest(Object event, Context context)
    {
      System.out.println("Handling request ");
      try {
        this.logger = LoggerFactory.getLogger(Handler.class) ; 
        logger.info("Example log from {}", event.getClass());
    
        logger.error("Fake error sorry i just pressed the wrong button");
      } catch (Exception e) {
        e.printStackTrace();
      }

      
      return "Dzien dobry";
    }
  }
  

// public class Handler  implements RequestHandler<SQSEvent, String>{

//     private LambdaLogger logger;
//     private final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public String handleRequest(SQSEvent event, Context context) {

//         // 0. Setup 

//         // 0.1 Logger
//         this.logger = context.getLogger();
        
//         for(SQSMessage message : event.getRecords()){

//             logger.log("Starting processing Message with Id :" + message.getMessageId(),LogLevel.INFO) ; 
            
//             try {    
        
//                 // 1. Extract Message content
//                 AnalyzedInvoiceDTO analyzedDocument =  parseJson(message.getBody(), AnalyzedInvoiceDTO.class, objectMapper) ;
        
//                 logger.log("Succesfully parsed Message with Id :" + message.getMessageId(),LogLevel.INFO) ; 
        
//                 // 2. Write Value to API 
//                 // TODO 

//                 logger.log("Successfully processed message with Id" + message.getMessageId(),LogLevel.INFO) ; 

//             } catch (Exception e) {
//                 logger.log("Failed processed message with Id" + message.getMessageId() + " with err " + e.getMessage() ,LogLevel.ERROR) ; 
//             }
        
//         }
        
//         return "null" ; 
//     }

//     public static <T> T parseJson(String jsonMessage, Class<T> clazz, ObjectMapper objectMapper) {
        
//         try {
//             return objectMapper.readValue(jsonMessage, clazz);
//         } catch (Exception e) {
//             throw new RuntimeException("Error parsing JSON message", e);
//         }
//     }


//     // private Connection getConnection(DatabaseConfig dbConfig){
//     //     try {
//     //         return DriverManager.getConnection(
//     //             dbConfig.getUrl() , // "jdbc:postgresql://localhost:5432/yourdatabase"  
//     //             dbConfig.getUsername() ,  
//     //             dbConfig.getPassword()
//     //          );            
//     //     } catch (SQLException e) {
//     //         logger.log(e.getMessage(), LogLevel.ERROR);
//     //         return null ; 
//     //     }

//     // }
// }
