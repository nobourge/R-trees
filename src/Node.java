import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;

import java.awt.*;
import java.util.List;

public class Node {
    private List<Node> children;

    public Node() {
        this.children = null;
    }
//    private Envelope mbr;
    private ReferencedEnvelope mbr;
//    private Rectangle mbr;

//    protected Envelope getMBR() {
    protected ReferencedEnvelope getMBR() {
//    protected Rectangle getMBR() {
        return this.mbr;
    }

    public List<Node> getChildren() {
        return children;
    }
}
