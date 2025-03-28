package loadAnalyzedDocument.dtos;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.textract.model.Block;

public class BlockDTO {

    public String id;
    public Float confidence ; 
    public String blockType;

    public List<String> entityTypes ; 

    public int columnIndex ; 
    public int columnSpan ; 
    public int rowIndex ; 
    public int rowSpan ; 

    public String textType ; 
    public String text ;     
    
    public List<RelationshipDTO> relationships;
    public GeometryDTO geometry ; 

    public String selectionStatus ; 

    public BlockDTO(Block block) {

        this.id = block.id();
    
        this.confidence = block.confidence() ;

        this.blockType = block.blockTypeAsString();

        this.entityTypes = block.entityTypesAsStrings() ; 

        this.columnIndex = block.columnIndex() ; 
        this.columnSpan = block.columnSpan() ;
        this.rowIndex = block.rowIndex() ; 
        this.rowSpan = block.rowSpan() ;


        this.textType = block.textTypeAsString() ; 
        this.text = block.text() ; 

        this.relationships = (block.relationships() != null)
            ? block.relationships().stream().map(r -> new RelationshipDTO(r) ).collect(Collectors.toList())
            : null;

        this.geometry = (block.geometry() != null) ? new GeometryDTO(block.geometry()) : null;


        this.selectionStatus = block.selectionStatusAsString() ; 
    }

    public BlockDTO(){};
}
