package loadAnalyzedTextToDb.dtos;

import software.amazon.awssdk.services.textract.model.Point;

public class PointDTO {
    public float x;
    public float y;

    public PointDTO(Point point) {
        if(point != null){
            this.x = point.x();
            this.y = point.y();
        }

    } 
}
