import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

public class Node {
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(Node.class);

    private List<Node> children;
    private Node parent;

    public Node() {
        this.children = null;
    }
//    private Envelope mbr;
    private ReferencedEnvelope mbr;
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
}
