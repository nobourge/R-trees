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
    private List<RNode> childrenRNode;
    private List<RLeaf> childrenRLeaf;
    private RNode parent;

    private ReferencedEnvelope mbr; // Minimum Bounding Rectangle
    public RNode(List<RNode> childrenRNode, List<RLeaf> childrenRLeaf) {
        logger.debug("RNode()");
    //        this.maxChildren = 4;
    //        this.minChildren = 2;

        this.childrenRNode = childrenRNode;
        this.childrenRLeaf = childrenRLeaf;
        updateMBR();

    }
    public RNode() {
        logger.debug("Node()");
//        this.children = new list of nodes which is empty:
        this.childrenRNode = new ArrayList<RNode>();
        this.childrenRLeaf = new ArrayList<RLeaf>();

    }



//    public List<Polygon> getPolygons() {
//        logger.debug("getPolygons()");
//        return polygons;
//    }

    public void addChildRNode(RNode child) {
        logger.debug("addChild()");
        childrenRNode.add(child);
        child.setParent(this);
        updateMBR();
    }

    public void addChildRLeaf(RLeaf child) {
        logger.debug("addChild()");
        childrenRLeaf.add(child);
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
        childrenRLeaf.add(leaf);
        updateMBR();
    }

//    public void addPolygon(Polygon polygon) {
//        logger.debug("addPolygon()");
//        polygons.add(polygon);
//        updateMBR();
//    }

    public void removeChild(RNode child) {
        logger.debug("removeChild()");
        childrenRNode.remove(child);
        updateMBR();
    }

//    public void removePolygon(Polygon polygon) {
//        logger.debug("removePolygon()");
//        polygons.remove(polygon);
//        updateMBR();
//    }

    void updateMBR() {
        logger.debug("updateMBR()");

        if (childrenRNode.size() == 0) {
            mbr = null;
            if (childrenRLeaf.size() == 0)
            {
                mbr = null;
            }

            else
            {
                for (RLeaf child : childrenRLeaf) {
                    ReferencedEnvelope childMBR = child.getMBR();
                    mbr.expandToInclude(childMBR);
                }
            }

        }

        else {
            for (RNode child : childrenRNode) {
                ReferencedEnvelope childMBR = child.getMBR();
                mbr.expandToInclude(childMBR);
            }
        }
    }


    public void setMBR(ReferencedEnvelope childEnvelope) {
        this.mbr = childEnvelope;
    }
}
