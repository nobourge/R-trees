package be.ulb.infof203.projet;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

public class RLeaf extends Node {
    private static final Logger logger = LoggerFactory.getLogger(RLeaf.class);
    private MultiPolygon polygon;

    public RLeaf(MultiPolygon polygon, String label) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger nologger = loggerContext.getLogger(RLeaf.class);
        nologger.setLevel(Level.OFF);


        logger.debug("RLeaf()");
        this.polygon = polygon;

        super.setMBR(polygon.getEnvelopeInternal());
        double area = getMBR().getArea();
        logger.debug("area: " + area);
        this.label = label;
        logger.debug("label: " + label);
        logger.debug("RLeaf() done");
    }
    public MultiPolygon getPolygon() {
        logger.debug("getPolygon()");
        return this.polygon;
    }
    @Override
    public String getInfo() {
        logger.debug("getInfo()");
        return "RLeaf: " + label + " " + getMBR().toString();
    }

    @Override
    boolean isLeaf() {
        return true;
    }

    @Override
    List<Node> getChildren() {
        logger.debug("getChildren()");
        return null;
    }

    @Override
    public String showInfo() {
        return "RLeaf: " + label + " " + getMBR().toString();
    }

    @Override
    void setLabel(String label) {
        this.label = label;
    }
}
