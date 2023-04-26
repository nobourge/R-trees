package be.ulb.infof203.projet;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.geotools.geometry.jts.JTS.toGeometry;


abstract class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    private Node parent;
    protected Envelope mbr;


    protected Node() {
        logger.debug("Node()");
    }

//    abstract Node(List<Node> children) {}
    protected void setMBR(Envelope envelopeInternal) {
        mbr = envelopeInternal;
    }
    protected Envelope getMBR() {
        logger.debug("getMBR()");
//        logger.debug("mbr: " + mbr);
        logger.debug("mbr.getArea(): " + mbr.getArea());
        return this.mbr;
    }

    protected double getMBRArea() {
        logger.debug("getMBRArea()");
        return this.mbr.getArea();
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


    abstract boolean isLeaf();

    abstract void removeChild(RNode rnode);

    abstract void addChild(RNode node1);

    abstract MultiPolygon getPolygon();
}
