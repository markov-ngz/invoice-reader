package loadAnalyzedTextToDb.dtos;

import software.amazon.awssdk.services.textract.model.Point;

class PointDTO {
    public float x;
    public float y;

    public PointDTO(Point point) {
        this.x = point.x();
        this.y = point.y();
    }
    public PointDTO() {
    }
}
