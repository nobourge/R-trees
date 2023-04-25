package be.ulb.infof203.projet;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GenericNode {
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

    private static final Logger logger = LoggerFactory.getLogger(GenericNode.class);
    private String label;

    private GenericNode parent;
    //list of children of type Node or Leaf:
    private List<GenericNode> children;
    private MultiPolygon polygon=null;

    private Envelope mbr; // Minimum Bounding Rectangle

    public GenericNode() {
        logger.debug("GenericNode()");
        children = new ArrayList<GenericNode>();
    }


    public GenericNode(MultiPolygon polygon) {
        logger.debug("GenericNode()");
        children = null;
        this.polygon = polygon;
    }
    public GenericNode(List<GenericNode> children) {
        this.children = children;
        updateMBR();
    }
    public GenericNode(MultiPolygon polygon, String label) {
        logger.debug("GenericNode()");
        children = null;
        this.polygon = polygon;
        this.label = label;
    }

    void updateMBR() {
        logger.debug("updateMBR()");

        if (children.size() == 0) {
            if (polygon == null) {
                logger.error("node must have minimum 1 child or a polygon");
            }
            mbr = polygon.getEnvelopeInternal();
            return;
        }

        mbr = children.get(0).getMBR();
        for (GenericNode child : children) {
            Envelope childMBR = child.getMBR();
            //
            mbr.expandToInclude(childMBR);
        }
    }


    public void setMBR(Envelope childEnvelope) {
        this.mbr = childEnvelope;
    }

    public void addChild(GenericNode child) {
        logger.debug("addChild()");
        children.add(child);
        child.setParent(this);
        updateMBR();
    }

    public void removeChild(GenericNode child) {
        logger.debug("removeChild()");
        children.remove(child);
        child.setParent(null);
        updateMBR();
    }

    public List<GenericNode> getChildren() {
        logger.debug("getChildren()");
        return children;
    }

    public void setChildren(List<GenericNode> children) {
        logger.debug("setChildren()");
        this.children = children;
        updateMBR();
    }

    public Envelope getMBR() {
        logger.debug("getMBR()");
        if (mbr == null) {
            logger.error(" id: " + getLabel());
            logger.error(" depth: " + getDepth());
            logger.error("mbr null");

            updateMBR();
            if (mbr == null) {
                logger.error("updateMBR did not work");
            }
        }
        return mbr;
    }

    public int getDepth() {
        int depth=0;
        GenericNode parentCurrent = parent;
        while (!parentCurrent.isRoot()) {
            parentCurrent = parent.getParent();
            depth++;
        }
        return depth;
    }

    public void setParent(GenericNode parent) {
        logger.debug("setParent()");
        this.parent = parent;
    }

    public GenericNode getParent() {
        logger.debug("getParent()");
        return parent;
    }

    public boolean isLeaf() {
        logger.debug("isLeaf()");
        return polygon != null;
    }

    public boolean isRoot() {
        logger.debug("isRoot()");
        return parent == null;
    }

    public boolean isInternal() {
        logger.debug("isInternal()");
        return !isLeaf();
    }


    public MultiPolygon getPolygon() {
        logger.debug("getPolygon()");
        if (polygon != null) {
            return polygon;
        }
        return null;
    }

    public String getLabel() {
        logger.debug("getLabel()");
        if (label != null) {
            return label;
        }
        return null;
    }
}
