import org.locationtech.jts.geom.Envelope;

import java.awt.*;

public class Node {
//    private Envelope mbr;
    private Rectangle mbr;

//    protected Envelope getMBR() {
    protected Rectangle getMBR() {
        return this.mbr;
    }
}
