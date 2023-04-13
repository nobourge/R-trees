import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Envelope;

import java.util.logging.Logger;

public class RLeaf extends Node {
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(RLeaf.class);
    private Polygon polygon;
    private Envelope mbr;
    private String label;

    public RLeaf(Polygon polygon, String label) {
        logger.fine("RLeaf()");
        this.polygon = polygon;
        this.mbr = polygon.getEnvelopeInternal();
        this.label = label;
    }

    public Polygon getPolygon() {
        logger.fine("getPolygon()");
        return this.polygon;
    }

    @Override
    public Envelope getMBR() {
        logger.fine("getMBR()");
        return this.mbr;
    }

    public String getLabel() {
        logger.fine("getLabel()");
        return this.label;
    }
}
