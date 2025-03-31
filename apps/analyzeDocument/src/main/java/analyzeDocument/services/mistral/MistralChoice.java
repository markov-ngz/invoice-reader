package analyzeDocument.services.mistral;

import lombok.Data;

@Data
public class MistralChoice {
    private int index ; 
    private String finish_reason ;
    private MistralMessage message ; 

}
