package loadAnalyzedDocument.dtos;

import java.util.List;

import software.amazon.awssdk.services.textract.model.Relationship ; 

public class RelationshipDTO {
    
    public String relationshipType ; 
    public List<String> relatedBlockIds ; 

    public RelationshipDTO(Relationship relationship){

        this.relationshipType =  relationship.typeAsString() ; 
        this.relatedBlockIds = relationship.ids() ; 
    }

}
