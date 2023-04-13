
//import org.locationtech.jts.geom.Polygon;

import java.awt.Rectangle;
import java.util.ArrayList;
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
    private List<Polygon> polygons; //
    private Rectangle MBR; // Minimum Bounding Rectangle

    public RNode() {
        logger.fine("RNode()");

        children = new ArrayList<>();
        polygons = new ArrayList<>();
        MBR = new Rectangle();
    }

    public List<Node> getChildren() {
        logger.fine("getChildren()");
        return children;
    }

    public List<Polygon> getPolygons() {
        logger.fine("getPolygons()");
        return polygons;
    }

    public Rectangle getMBR() {
        logger.fine("getMBR()");
        return MBR;
    }

    public void addChild(Node child) {
        logger.fine("addChild()");
        children.add(child);
        updateMBR();
    }
    public void addLeaf(RLeaf leaf) {
        logger.fine("addLeaf()");
        children.add(leaf);
        updateMBR();
    }

    public void addPolygon(Polygon polygon) {
        logger.fine("addPolygon()");
        polygons.add(polygon);
        updateMBR();
    }

    public void removeChild(RNode child) {
        logger.fine("removeChild()");
        children.remove(child);
        updateMBR();
    }

    public void removePolygon(Polygon polygon) {
        logger.fine("removePolygon()");
        polygons.remove(polygon);
        updateMBR();
    }

    private void updateMBR() {
        logger.fine("updateMBR()");
        MBR = new Rectangle();
        for (Node child : children) {
            MBR = MBR.union(child.getMBR());
        }
        for (Polygon polygon : polygons) {
            MBR = MBR.union(polygon.getMBR());
        }
    }

    public void expandMbr(Polygon polygon) {
    }
}
