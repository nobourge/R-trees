package be.ulb.infof203.projet;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

public class RLeaf extends Node {
    private static final Logger logger = LoggerFactory.getLogger(RLeaf.class);
    private MultiPolygon polygon;
    private String label;

    public RLeaf(MultiPolygon polygon, String label) {
        logger.debug("RLeaf()");
        this.polygon = polygon;

        super.setMBR(polygon.getEnvelopeInternal());
        double area = getMBR().getArea();
        logger.debug("area: " + area);
        this.label = label;
        logger.debug("label: " + label);
        logger.debug("RLeaf() done");
    }

    public RLeaf(String Label) {
        this.label = Label;
    }

    public MultiPolygon getPolygon() {
        logger.debug("getPolygon()");
        return this.polygon;
    }

    @Override
    List<Node> getChildren() {
        return null;
    }

    @Override
    String getLabel() {
        return label;
    }

    @Override
    boolean isLeaf() {
        return true;
    }

    @Override
    void removeChild(RNode rnode) {

    }

    @Override
    void addChild(RNode node1) {

    }
}
