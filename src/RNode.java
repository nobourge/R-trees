import org.geotools.geometry.jts.ReferencedEnvelope;

import java.util.List;
import java.util.logging.Logger;


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
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(RNode.class);


    //list of children of type RNode or RLeaf:
    private List<Node> children;
    private RNode parent;

    private ReferencedEnvelope mbr; // Minimum Bounding Rectangle

    public RNode(List<Node> children) {
        logger.fine("RNode()");
    //        this.maxChildren = 4;
    //        this.minChildren = 2;

        this.children = children;
        updateMBR();

    }



//    public List<Polygon> getPolygons() {
//        logger.fine("getPolygons()");
//        return polygons;
//    }

    public void addChild(Node child) {
        logger.fine("addChild()");
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
        logger.fine("addLeaf()");
        children.add(leaf);
        updateMBR();
    }

//    public void addPolygon(Polygon polygon) {
//        logger.fine("addPolygon()");
//        polygons.add(polygon);
//        updateMBR();
//    }

    public void removeChild(RNode child) {
        logger.fine("removeChild()");
        children.remove(child);
        updateMBR();
    }

//    public void removePolygon(Polygon polygon) {
//        logger.fine("removePolygon()");
//        polygons.remove(polygon);
//        updateMBR();
//    }

    void updateMBR() {
        logger.fine("updateMBR()");

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
