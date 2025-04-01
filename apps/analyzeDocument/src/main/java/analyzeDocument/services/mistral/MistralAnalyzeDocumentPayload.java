package analyzeDocument.services.mistral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MistralAnalyzeDocumentPayload {
    public String model ;

    @JsonIgnore
    public String role = "user" ;

    public List<MistralMessage> messages = new ArrayList<MistralMessage>() ; 
    
    public int document_image_limit = 8 ; 
    public int document_page_limit = 64 ; 
    public Map<String,String> response_format = new HashMap<String,String>() ; 

    public MistralAnalyzeDocumentPayload(String model, String prompt, String documentUrl  ){
        
        this.model = model ;

        response_format.put("type", "json_object") ; 

        MistralMessage mistralMessage = new MistralMessage(this.role, prompt, documentUrl) ; 
        this.messages.add(mistralMessage) ; 
    }

    
    public MistralAnalyzeDocumentPayload(String model, String prompt){
        this.model = model ;

        response_format.put("type", "json_object") ; 
        
        MistralMessage mistralMessage = new MistralMessage(prompt) ; 
        this.messages.add(mistralMessage) ; 
    }
}
