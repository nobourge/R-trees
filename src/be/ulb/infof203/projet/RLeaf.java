package be.ulb.infof203.projet;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

public class RLeaf extends Node {
    private static final Logger logger = LoggerFactory.getLogger(RLeaf.class);
    private MultiPolygon polygon;
    private String label;

    public RLeaf(MultiPolygon polygon, String label) {
        logger.debug("RLeaf()");
        this.polygon = polygon;
        super.setMBR(polygon.getEnvelopeInternal());
        // transform Envelope e into Rectangle r
//        this.mbr = new Rectangle((int)envelope.getMinX(), (int)envelope.getMinY(),
//                (int)envelope.getWidth(), (int)envelope.getHeight());
        this.label = label;
    }
    public MultiPolygon getPolygon() {
        logger.debug("getPolygon()");
        return this.polygon;
    }
    public String getLabel() {
        logger.debug("getLabel()");
        return this.label;
    }

    @Override
    List<Node> getChildren() {
        return null;
    }
}
