package loadAnalyzedDocument;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper ; 

import loadAnalyzedDocument.dtos.AnalyzedInvoiceDTO;
import loadAnalyzedDocument.dtos.InvoiceDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.slf4j.LoggerFactory;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;


public class Handler implements RequestHandler<SQSEvent, String>{


    private Logger logger ;

    private ObjectMapper objectMapper ;  

    private final String uri = "" ; 
  
    @Override
    public String handleRequest(SQSEvent event, Context context)
    {
      this.logger = LoggerFactory.getLogger(Handler.class) ; 
      logger.info("Starting");
      for(SQSMessage message :  event.getRecords()){
        try {
          // extract response
          AnalyzedInvoiceDTO analyzedInvoiceDTO = objectMapper.readValue(message.getBody(), AnalyzedInvoiceDTO.class) ; 

          logger.info("Successfully parsed payload for message {}", message.getMessageId());

          for(InvoiceDTO invoiceDTO : analyzedInvoiceDTO.getInvoiceDTOs()){
            
            // serialize 
            String payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(invoiceDTO) ; 

            // send request 
            HttpPost httpPost = new   HttpPost(uri) ; 
            
            StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON) ; 

            httpPost.setEntity(entity);

            try(CloseableHttpClient httpClient = HttpClients.createDefault()){
                return  httpClient.execute(httpPost, response -> {
                    if(response.getCode() >= 400 ){
                      throw new HttpException(String.format("Invalid status code received %s",response.getCode())) ; 
                    }else{
                      return null ;
                    }
              });
            }
          }
    
        } catch (JsonMappingException e ) {
            e.printStackTrace();
            logger.error("Failed to parse message body", e);
            continue ;          
        } catch (Exception e) {
          e.printStackTrace();
          logger.error("Unexpected error occurred", e);
          continue ; 
        }          
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
