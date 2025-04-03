package analyzeDocument.services.mistral;

import lombok.Data;

@Data
public class MistralChoiceDTO {
    int index ; 
    String finish_reason ;
    MistralMessageDTO message ; 
}
