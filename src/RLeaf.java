import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Envelope;

import java.awt.*;
import java.util.logging.Logger;

public class RLeaf extends Node {
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(RLeaf.class);
    private Polygon polygon;
//    private Envelope mbr;
    private Rectangle mbr;
    private String label;

    public RLeaf(Polygon polygon, String label) {
        logger.fine("RLeaf()");
        this.polygon = polygon;
        Envelope envelope = polygon.getEnvelopeInternal();
        // transform Envelope e into Rectangle r

        Rectangle rect = new Rectangle((int)envelope.getMinX(), (int)envelope.getMinY(),
                (int)envelope.getWidth(), (int)envelope.getHeight());

        this.mbr = rect;
        this.label = label;
    }

    public Polygon getPolygon() {
        logger.fine("getPolygon()");
        return this.polygon;
    }

    @Override
//    public Envelope getMBR() {
    public Rectangle getMBR() {
        logger.fine("getMBR()");
        return this.mbr;
    }

    public String getLabel() {
        logger.fine("getLabel()");
        return this.label;
    }
}
