package be.ulb.infof203.projet;

import be.ulb.infof203.projet.RTree;
import org.locationtech.jts.geom.*;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.geometry.jts.WKTWriter2;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.geotools.referencing.crs.DefaultTemporalCRS;
import org.geotools.referencing.crs.DefaultVerticalCRS;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.geotools.referencing.datum.DefaultGeodeticDatum;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.geotools.styling.SLD;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.Intersects;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;



import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

public class RTreeTestMemory {
    public static void main(String[] args) {
        int maxChildren = 10;
        int minChildren = 5;
        int maxDepth = 5;
        int minDepth = 2;

        // create RTree
        RTree rTree = new RTree(maxChildren, minChildren, maxDepth, minDepth);

        // generate random data and insert into RTree
        List<MultiPolygon> data = generateData(1000);
        for (MultiPolygon polygon : data) {
            rTree.addLeaf((RNode) rTree.getRoot(), "label", polygon, "quadratic");
        }

        // measure search time for a specific node
        MultiPolygon target = data.get(500);
        long start = System.currentTimeMillis();




        GeometryFactory geometryFactory = new GeometryFactory();
        //TODO MODIF POINT
        Coordinate pointCoord = new Coordinate(0, 1);
        Point point = geometryFactory.createPoint(pointCoord);

        double buffer = 0.001; // adjust buffer size as needed
        Envelope envelope = new Envelope(pointCoord);
        envelope.expandBy(buffer);
        //TODO CALL SEARCH METHOD BUT ERRORS

        long end = System.currentTimeMillis();
        System.out.println("Search time for target node: " + (end - start) + " ms");

        // measure memory usage of RTree
        long memoryUsed = measureMemoryUsage(rTree);
        System.out.println("Memory used by RTree: " + memoryUsed + " bytes");
    }

    private static List<MultiPolygon> generateData(int numPolygons) {
        List<MultiPolygon> data = new ArrayList<>();
        for (int i = 0; i < numPolygons; i++) {
            double x = Math.random() * 100;
            double y = Math.random() * 100;
            double width = Math.random() * 10;
            double height = Math.random() * 10;
            Coordinate[] coords = new Coordinate[] {
                    new Coordinate(x, y),
                    new Coordinate(x + width, y),
                    new Coordinate(x + width, y + height),
                    new Coordinate(x, y + height),
                    new Coordinate(x, y)
            };
            Polygon polygon = new GeometryFactory().createPolygon(coords);
            //data.add(new MultiPolygon(new Polygon[] {polygon}));
        }
        return data;
    }

    private static long measureMemoryUsage(RTree tree) {
        Runtime runtime = Runtime.getRuntime();
        long memoryUsedBefore = runtime.totalMemory() - runtime.freeMemory();

        // trigger garbage collection
        System.gc();

        long memoryUsedAfter = runtime.totalMemory() - runtime.freeMemory();

        return memoryUsedAfter - memoryUsedBefore;
    }

}