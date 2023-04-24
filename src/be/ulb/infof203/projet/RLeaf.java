package be.ulb.infof203.projet;

import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Envelope;

import java.awt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class RLeaf extends Node {
    private static final Logger logger = LoggerFactory.getLogger(RLeaf.class);
    private Polygon polygon;
//    private Envelope mbr;
    private Rectangle mbr;
    private String label;

    public RLeaf(Polygon polygon, String label) {
        logger.debug("RLeaf()");
        this.polygon = polygon;
        Envelope envelope = polygon.getEnvelopeInternal();
        // transform Envelope e into Rectangle r

        this.mbr = new Rectangle((int)envelope.getMinX(), (int)envelope.getMinY(),
                (int)envelope.getWidth(), (int)envelope.getHeight());
        this.label = label;
    }

    public Polygon getPolygon() {
        logger.debug("getPolygon()");
        return this.polygon;
    }


    public String getLabel() {
        logger.debug("getLabel()");
        return this.label;
    }
}
