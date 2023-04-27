package be.ulb.infof203.projet;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.geotools.geometry.jts.JTS.toGeometry;


abstract class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    // Disable logger :


    protected RNode parent;
    protected String label;
    protected Envelope mbr;


    protected Node() {


        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger nologger = loggerContext.getLogger(Node.class);
        nologger.setLevel(Level.OFF);
        logger.debug("Node()");
    }

    protected void setMBR(Envelope envelopeInternal) {
        mbr = envelopeInternal;
    }
    protected Envelope getMBR() {
//        logger.debug("getMBR()");
//        logger.debug("mbr: " + mbr);
//        logger.debug("mbr.getArea(): " + mbr.getArea());
        return this.mbr;
    }
    protected double getMBRArea() {
//        logger.debug("getMBRArea()");
        return this.mbr.getArea();
    }
    public RNode getParent() {
        logger.debug("getParent()");
        return parent;
    }
    public void setParent(RNode parent) {
        logger.debug("setParent()");
        this.parent = parent;
    }
    abstract boolean isLeaf();

    abstract String getInfo();

    abstract List<Node> getChildren();

    abstract String showInfo();

    abstract void setLabel(String label);

    public String getLabel() {
        return label;
    }
}
