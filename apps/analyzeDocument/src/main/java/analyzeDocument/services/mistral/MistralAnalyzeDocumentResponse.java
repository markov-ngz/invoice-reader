package analyzeDocument.services.mistral;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class MistralAnalyzeDocumentResponse {
    String id ; 
    String object ; 
    BigInteger created ;
    String model ;
    List<MistralChoiceDTO> choices ;
    Map<String,Integer> usage ; 
}
