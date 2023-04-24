package be.ulb.infof203.projet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.geotools.geometry.jts.JTS.toGeometry;


public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    private List<Node> children;
    private Node parent;

    public Node() {
        logger.debug("Node()");
//        this.children = new list of nodes which is empty:
        this.children = new ArrayList<>();

    }
//    private Envelope mbr;
    private ReferencedEnvelope mbr;
    private Polygon polygon;
//    private Rectangle mbr;

//    protected Envelope getMBR() {
    protected ReferencedEnvelope getMBR() {
//    protected Rectangle getMBR() {
        return this.mbr;
    }

    public List<Node> getChildren() {
//        logger.debug("getChildren()");
        return this.children;
    }

    public Node getParent() {
        logger.debug("getParent()");
        return parent;
    }

    public void setParent(Node parent) {
        logger.debug("setParent()");
        this.parent = parent;
    }

    public Polygon getPolygon() {
        if (polygon == null) {
//            return mbr.toGeometry();
            return toGeometry(mbr);
        }
        return polygon;
    }
}
