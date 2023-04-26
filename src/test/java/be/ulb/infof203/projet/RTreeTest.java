package be.ulb.infof203.projet;

import java.util.Random;

import be.ulb.infof203.projet.RNode;
import be.ulb.infof203.projet.RTree;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class RTreeTest {
    //Create 1000 Polygons que l'on ajoute à la RTree en utilisant la méthode
    //quadratic.
    public static void main(String[] args) {
        RTree rtree = new RTree(10, 5, 10, 5);
        GeometryFactory gf = new GeometryFactory();
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            Coordinate[] coords = new Coordinate[5];
            for (int j = 0; j < 5; j++) {
                coords[j] = new Coordinate(rand.nextDouble() * 100, rand.nextDouble() * 100);
            }
            coords[4] = coords[0];
            Polygon poly = gf.createPolygon(coords);
            MultiPolygon mp = gf.createMultiPolygon(new Polygon[]{poly});
            rtree.addLeaf((RNode) rtree.getRoot(), "label_" + i, mp, "quadratic");
        }

        System.out.println("RTree depth: " + rtree.getDepth());
        System.out.println("Number of nodes: " + rtree.getNodeQuantity());
        System.out.println("Number of leaves: " + rtree.getLeafQuantity());
        System.out.println("Number of RNodes: " + rtree.getRNodeQuantity());
    }
}
