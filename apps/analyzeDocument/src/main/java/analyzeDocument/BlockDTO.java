package analyzeDocument;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BoundingBox;
import software.amazon.awssdk.services.textract.model.Geometry;
import software.amazon.awssdk.services.textract.model.Point;



class PointDTO {
    public float x;
    public float y;

    public PointDTO(Point point) {
        this.x = point.x();
        this.y = point.y();
    }
}

class GeometryDTO {
    public float width;
    public float height;
    public float left;
    public float top;
    public List<PointDTO> polygon;

    public GeometryDTO(Geometry geometry) {
        if (geometry.boundingBox() != null) {
            BoundingBox bbox = geometry.boundingBox();
            this.width = bbox.width();
            this.height = bbox.height();
            this.left = bbox.left();
            this.top = bbox.top();
        }
        this.polygon = (geometry.polygon() != null)
            ? geometry.polygon().stream().map(PointDTO::new).collect(Collectors.toList())
            : null;
    }
}


class BlockDTO {
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