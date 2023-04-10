import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Envelope;

public class RLeaf {
    private Polygon polygon;
    private Envelope mbr;
    private String label;

    public RLeaf(Polygon polygon, String label) {
        this.polygon = polygon;
        this.mbr = polygon.getEnvelopeInternal();
        this.label = label;
    }

    public Polygon getPolygon() {
        return this.polygon;
    }

    public Envelope getMBR() {
        return this.mbr;
    }

    public String getLabel() {
        return this.label;
    }
}
