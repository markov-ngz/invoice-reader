package analyzeDocument.services.mistral;

import java.math.BigInteger;

import lombok.Data;

@Data
public class MistralUploadFileResponse {
    private String id ; 
    private String object ; 
    private int bytes ; 
    private BigInteger created_at ; 
    private String filename ; 
    private String purpose ; 
    private String sample_type ; 
    private int num_lines ; 
    private String source ; 

}
