package be.ulb.infof203.projet;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
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
    private List<Node> children;

    public RNode(int label) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger nologger = loggerContext.getLogger(RNode.class);
        nologger.setLevel(Level.OFF);

        logger.debug("RNode()");
        setLabel(String.valueOf(label));
          children = new ArrayList<>();
    }

    public RNode(List<Node> nodes, int label) {
        logger.debug("RNode()");
        setLabel(String.valueOf(label));
        children = nodes;
        updateMBR();
    }

    public double getMBRAreaIfExpandedToInclude(Envelope envelope) {
        logger.debug("getMBRArea()");
        logger.debug("envelope: " + envelope);
        logger.debug("envelope.getArea(): " + envelope.getArea());
        Envelope mbrCopy = new Envelope(mbr);
        mbrCopy.expandToInclude(envelope);
        return mbrCopy.getArea();
    }
    public void addChild(Node child) {
        logger.debug("addChild()");
        logger.debug("child: " + child);
        logger.debug("child type: " + child.getClass());
        // if child is instance of a class different from the class of the first child
        // then throw an exception
        if (!children.isEmpty()) {
            logger.debug("children is not empty");
            logger.debug("children list type: " + children.getClass());
            logger.debug("children.size(): " + children.size());
            logger.debug("children.get(0).getClass(): " + children.get(0).getClass());
            if (child.getClass() != children.get(0).getClass()) {
                throw new IllegalArgumentException("Child is not of the same type as the other children");
            }
        }
        //TODO FIX Error dans RTreeTest
        children.add(child); // UnsupportedOperationException – the add operation is not supported by this list when
        child.setParent(this);
        showChildren();
        updateMBR(child);
    }

    private void showChildren() {
        logger.debug("showChildren()");
        for (Node child : children) {
            logger.debug("child: " + child);
            logger.debug("child type: " + child.getClass());
        }
    }
    public void removeChild(RNode child) {
        logger.debug("removeChild()");
        children.remove(child);
        updateMBR();
    }
    public void removeChild(RLeaf child) {
        logger.debug("removeChild()");
        children.remove(child);
        updateMBR();
    }
    void updateMBR(Node child) {
        logger.debug("updateMBR()");
        Envelope childMBR = child.getMBR();
        if (mbr == null) {
            mbr = childMBR;
        } else {
            mbr.expandToInclude(childMBR);
        }
        logger.debug("mbr: " + mbr);
        logger.debug("area: " + mbr.getArea());

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
    @Override
    List<Node> getChildren() {
//        logger.debug("getChildren()");
        return children;
    }

    @Override
    boolean isLeaf() {
        return false;
    }

    @Override
    String getInfo() {
//        logger.debug("getInfo()");
        StringBuilder info = new StringBuilder();
        for (Node child : children) {
            info.append(child.getInfo());
        }
        return info.toString();
    }

    int getChildQuantity() {
        return children.size();
    }

    @Override
    String showInfo() {
        StringBuilder info = new StringBuilder();
        info.append("RNode: ");
        info.append(getLabel());
//        info.append("children: ");
//        info.append(children.size()).append(": ");
//        for (Node child : children) {
//            info.append(",");
//            info.append(child.showInfo());
//        }
        return info.toString();
    }

    @Override
    void setLabel(String label) {
        this.label = label;
    }
}
