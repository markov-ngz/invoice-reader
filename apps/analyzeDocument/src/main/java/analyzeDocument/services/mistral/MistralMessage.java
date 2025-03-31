package analyzeDocument.services.mistral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;


@Data
public class MistralMessage {

    public String role ; 
    public List<Map<String,String>> content = new ArrayList<Map<String,String>>();
    public String tool_calls ; 
    
    public MistralMessage(String role , String text, String documentUrl){
        
        this.role = role ; 
        Map<String,String> contentPrompt = new HashMap<String,String>() ; 
        contentPrompt.put("type", "text") ; 
        contentPrompt.put("text", text) ; 
        content.add(contentPrompt) ; 

        Map<String,String> contentDocument = new HashMap<String,String>() ; 
        contentDocument.put("type", "document_url") ; 
        contentDocument.put("document_url", documentUrl) ;
        content.add(contentDocument);
    }

    public MistralMessage(String text){
        
        this.role = "user" ; 
        Map<String,String> contentPrompt = new HashMap<String,String>() ; 
        contentPrompt.put("type", "text") ; 
        contentPrompt.put("text", text) ; 

        content.add(contentPrompt) ; 
        
    }
}
