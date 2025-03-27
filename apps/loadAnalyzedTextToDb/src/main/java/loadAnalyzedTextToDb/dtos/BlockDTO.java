package loadAnalyzedTextToDb.dtos;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.textract.model.Block;

public class BlockDTO {
    public String blockType;
    public String id;
    public List<String> relationships;
    public String textType ; 
    public String text ; 

    public GeometryDTO geometry ; 


    public BlockDTO(Block block) {
        this.blockType = block.blockTypeAsString();
        this.id = block.id();
        this.textType = block.textTypeAsString() ; 
        this.text = block.text() ; 
        this.relationships = (block.relationships() != null)
            ? block.relationships().stream().map(r -> r.typeAsString()).collect(Collectors.toList())
            : null;
        this.geometry = (block.geometry() != null) ? new GeometryDTO(block.geometry()) : null;
    }
}