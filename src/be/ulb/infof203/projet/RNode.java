package be.ulb.infof203.projet;

import org.geotools.geometry.jts.ReferencedEnvelope;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
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
    private List<RNode> childrenRnode;
    private List<RLeaf> childrenRLeaf;

    private Envelope mbr; // Minimum Bounding Rectangle

//    public RNode(List<Node> children) {


    public RNode(List<RLeaf> childrenRleaf) {
        logger.debug("RNode()");

        this.childrenRLeaf = childrenRleaf;
        updateMBR();
    }
    public RNode() {
        logger.debug("RNode()");
        childrenRnode = new ArrayList<RNode>();
        childrenRLeaf = new ArrayList<RLeaf>();
    }
    public void addChild(RNode child) {
        logger.debug("addChild()");
        childrenRnode.add(child);
        child.setParent(this);
        updateMBR();
    }

    public void addChild(RLeaf child) {
        logger.debug("addChild()");
        childrenRLeaf.add(child);
        child.setParent(this);
        updateMBR();
    }

    public List<RNode> getChildrenRnode() {
        logger.debug("getChildren()");
        return childrenRnode;
    }
    public List<RLeaf> getChildren() {
        logger.debug("getChildren()");
        return childrenRLeaf;
    }


    public void addLeaf(RLeaf leaf) {
        logger.debug("addLeaf()");
        childrenRLeaf.add(leaf);
        updateMBR();
    }

    public void removeChild(RNode child) {
        logger.debug("removeChild()");
        childrenRnode.remove(child);
        updateMBR();
    }

    public void removeChild(RLeaf child) {
        logger.debug("removeChild()");
        childrenRLeaf.remove(child);
        updateMBR();
    }
    void updateMBR() {
        logger.debug("updateMBR()");

//        if (children.size() == 0) {
//            mbr = null;
//            return;
//        }
//
//        mbr = children.get(0).getMBR();
//        for (Node child : children) {
//            Envelope childMBR = child.getMBR();
//            //
//            mbr.expandToInclude(childMBR);
//        }
    }


    public void setMBR(Envelope childEnvelope) {
        this.mbr = childEnvelope;
    }
}
