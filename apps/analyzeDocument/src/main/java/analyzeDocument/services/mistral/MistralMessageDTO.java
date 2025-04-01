package analyzeDocument.services.mistral;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class MistralMessageDTO {
    public String role ; 
    public String content ;
    
    public List<Map<String,String>> tool_calls ;
    
}
