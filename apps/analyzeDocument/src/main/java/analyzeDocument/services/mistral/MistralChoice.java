package analyzeDocument.services.mistral;

import lombok.Data;

@Data
public class MistralChoice {
    int index ; 
    String finish_reason ;
    MistralMessage message ; 

}
