package be.ulb.infof203.projet;

import org.geotools.geometry.jts.ReferencedEnvelope;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
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
//    private List<RNode> children;
//    private List<RLeaf> children;
    private List<Node> children;


//    public RNode(List<Node> children) {
//    public RNode(List<RLeaf> childrenRleaf) {
//        logger.debug("RNode()");
//
//        this.childrenRLeaf = childrenRleaf;
//        updateMBR();
//    }
    public RNode() {
        logger.debug("RNode()");
//        childrenRnode = new ArrayList<RNode>();
//        childrenRLeaf = new ArrayList<RLeaf>();
          children = new ArrayList<Node>();
    }

    public RNode(List<Node> nodes) {
        logger.debug("RNode()");
//        childrenRnode = rNodes;
//        childrenRLeaf = new ArrayList<RLeaf>();
//        children = new ArrayList<Node>();
        children = nodes;
        updateMBR();
    }

    //    public void addChild(RNode child) {
//        logger.debug("addChild()");
//        children = new ArrayList<RNode>();
//
//        children.add(child);
//        child.setParent(this);
//        updateMBR();
//    }
//    public void addChild(RLeaf child) {
//        logger.debug("addChild()");
//        childrenRLeaf.add(child);
//        child.setParent(this);
//        updateMBR();
//    }
    public void addChild(Node child) {
        logger.debug("addChild()");
        logger.debug("child: " + child);
        logger.debug("child: " + child.getLabel());
        logger.debug("child type: " + child.getClass());
        // if child is instance of a class different from the class of the first child
        // then throw an exception
        if (!children.isEmpty()) {
            if (child.getClass() != children.get(0).getClass()) {
                throw new IllegalArgumentException("Child is not of the same type as the other children");
            }
        }
        children.add(child);
        child.setParent(this);
        showChildren();
        updateMBR();
    }

    private void showChildren() {
        logger.debug("showChildren()");
        for (Node child : children) {
            logger.debug("child: " + child);
            logger.debug("child: " + child.getLabel());
            logger.debug("child type: " + child.getClass());
        }
    }
//    public List<RNode> getChildrenRnode() {
//        logger.debug("getChildren()");
//        return childrenRnode;
//    }
//    public List<RLeaf> getChildren() {
//        logger.debug("getChildren()");
//        return childrenRLeaf;
//    }

    public void removeChild(RNode child) {
        logger.debug("removeChild()");
        children.remove(child);
        updateMBR();
    }

    @Override
    void addChild(RNode node1) {

    }

    @Override
    MultiPolygon getPolygon() {
        return null;
    }

    public void removeChild(RLeaf child) {
        logger.debug("removeChild()");
        children.remove(child);
        updateMBR();
    }
    void updateMBR() {
        logger.debug("updateMBR()");

        if (children.isEmpty()) {
            mbr = null;
            return;
        }

        mbr = children.get(0).getMBR();
        for (Node child : children) {
            Envelope childMBR = child.getMBR();
            mbr.expandToInclude(childMBR);
        }
        logger.debug("mbr: " + mbr);
        logger.debug("area: " + mbr.getArea());
    }


    public void setMBR(Envelope childEnvelope) {
        this.mbr = childEnvelope;
    }

    @Override
    List<Node> getChildren() {
        logger.debug("getChildren()");
        return children;
//        return (List<RNode>) children;
    }

    @Override
    String getLabel() {
        return null;
    }

    @Override
    boolean isLeaf() {
        return false;
    }
}
