import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

import static org.geotools.geometry.jts.JTS.toGeometry;


public class Node {
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(Node.class);

    private List<Node> children;
    private Node parent;

    public Node() {
        this.children = null;
    }
//    private Envelope mbr;
    private ReferencedEnvelope mbr;
    private Polygon polygon=null;
//    private Rectangle mbr;

//    protected Envelope getMBR() {
    protected ReferencedEnvelope getMBR() {
//    protected Rectangle getMBR() {
        return this.mbr;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        logger.fine("getParent()");
        return parent;
    }

    public void setParent(Node parent) {
        logger.fine("setParent()");
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
