
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;


public class RNode {
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(RNode.class);

    private List<RNode> children;
    private List<Polygon> polygons;
    private Rectangle MBR;

    public RNode() {
        logger.fine("RNode()");

        children = new ArrayList<>();
        polygons = new ArrayList<>();
        MBR = new Rectangle();
    }

    public List<RNode> getChildren() {
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

    public void addChild(RNode child) {
        logger.fine("addChild()");
        children.add(child);
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
        for (RNode child : children) {
            MBR = MBR.union(child.getMBR());
        }
        for (Polygon polygon : polygons) {
            MBR = MBR.union(polygon.getMBR());
        }
    }
}
