package loadAnalyzedTextToDb.dtos;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.textract.model.BoundingBox;
import software.amazon.awssdk.services.textract.model.Geometry;

public class GeometryDTO {
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
