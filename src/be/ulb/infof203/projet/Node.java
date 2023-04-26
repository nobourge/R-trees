package be.ulb.infof203.projet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.geotools.geometry.jts.JTS.toGeometry;


abstract class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    private Node parent;
    private Envelope mbr;


    public Node() {
        logger.debug("Node()");
    }
    protected void setMBR(Envelope envelopeInternal) {
        mbr = envelopeInternal;
    }
    protected Envelope getMBR() {
        return this.mbr;
    }

    public Node getParent() {
        logger.debug("getParent()");
        return parent;
    }

    public void setParent(Node parent) {
        logger.debug("setParent()");
        this.parent = parent;
    }

    abstract List<Node> getChildren();
    abstract String getLabel();




}
