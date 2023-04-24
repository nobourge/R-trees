import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.geotools.geometry.jts.JTS.toGeometry;


public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    private List<RNode> childrenRNode;
    private List<RLeaf> childrenRLeaf;
    private Node parent;

    public Node() {
        logger.debug("Node()");
//        this.children = new list of nodes which is empty:
        this.childrenRNode = new ArrayList<RNode>();
        this.childrenRLeaf = new ArrayList<RLeaf>();

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

    public List<RNode> getChildrenRNode() {
//        logger.debug("getChildren()");
        return this.childrenRNode;
    }

    public List<RLeaf> getChildrenRLeaf() {
//        logger.debug("getChildren()");
        return this.childrenRLeaf;
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
