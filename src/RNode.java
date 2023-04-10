
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class RNode {

    private List<RNode> children;
    private List<Polygon> polygons;
    private Rectangle MBR;

    public RNode() {
        children = new ArrayList<>();
        polygons = new ArrayList<>();
        MBR = new Rectangle();
    }

    public List<RNode> getChildren() {
        return children;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public Rectangle getMBR() {
        return MBR;
    }

    public void addChild(RNode child) {
        children.add(child);
        updateMBR();
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
        updateMBR();
    }

    public void removeChild(RNode child) {
        children.remove(child);
        updateMBR();
    }

    public void removePolygon(Polygon polygon) {
        polygons.remove(polygon);
        updateMBR();
    }

    private void updateMBR() {
        MBR = new Rectangle();
        for (RNode child : children) {
            MBR = MBR.union(child.getMBR());
        }
        for (Polygon polygon : polygons) {
            MBR = MBR.union(polygon.getMBR());
        }
    }
}
