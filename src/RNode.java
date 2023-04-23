import org.geotools.geometry.jts.ReferencedEnvelope;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RNode extends Node{
    // Une feuille contiendra
    // un (multi-)polygone
    // , le MBR associé
    // , ainsi qu’un label.
    //
    // Un nœud
    //contiendra
    // une liste de descendants (nœuds ou feuilles)
    // , ainsi qu’un MBR correspondant
    //      à l’union de l’ensemble des MBR de ses descendants.

    private static final Logger logger = LoggerFactory.getLogger(RNode.class);

    //list of children of type RNode or RLeaf:
    private List<Node> children;
    private RNode parent;

    private ReferencedEnvelope mbr; // Minimum Bounding Rectangle

    public RNode(List<Node> children) {
        logger.debug("RNode()");
    //        this.maxChildren = 4;
    //        this.minChildren = 2;

        this.children = children;
        updateMBR();

    }
    public RNode() {
        logger.debug("Node()");
//        this.children = new list of nodes which is empty:
        this.children = new ArrayList<Node>();

    }



//    public List<Polygon> getPolygons() {
//        logger.debug("getPolygons()");
//        return polygons;
//    }

    public void addChild(Node child) {
        logger.debug("addChild()");
        children.add(child);
        child.setParent(this);
        updateMBR();
    }

    public void setParent(RNode parent) {
        this.parent = parent;
    }

    public RNode getParent() {
        return parent;
    }

    public void addLeaf(RLeaf leaf) {
        logger.debug("addLeaf()");
        children.add(leaf);
        updateMBR();
    }

//    public void addPolygon(Polygon polygon) {
//        logger.debug("addPolygon()");
//        polygons.add(polygon);
//        updateMBR();
//    }

    public void removeChild(RNode child) {
        logger.debug("removeChild()");
        children.remove(child);
        updateMBR();
    }

//    public void removePolygon(Polygon polygon) {
//        logger.debug("removePolygon()");
//        polygons.remove(polygon);
//        updateMBR();
//    }

    void updateMBR() {
        logger.debug("updateMBR()");

        if (children.size() == 0) {
            mbr = null;
            return;
        }

        mbr = children.get(0).getMBR();
        for (Node child : children) {
            ReferencedEnvelope childMBR = child.getMBR();
            //
            mbr.expandToInclude(childMBR);
        }
    }


    public void setMBR(ReferencedEnvelope childEnvelope) {
        this.mbr = childEnvelope;
    }
}
