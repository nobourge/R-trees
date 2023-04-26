package be.ulb.infof203.projet;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;


import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class MainTest {

    /**
     * This test method tests the searchTree method by checking if it returns a feature with the expected geometry.
     * @throws IOException if there is an error reading the file
     * @throws Exception if there is an error in the searchTree method*/
    @Test
    public void testSearchTree() throws IOException, Exception{
        String fileName = "";
        SimpleFeatureCollection features = Main.getSimpleFeatureCollection(fileName);
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(4.353735, 50.846854));
        SimpleFeature feature = Main.searchTree(features, point, "quadtree", null);
        assertNotNull(feature);
        assertEquals("MultiPolygon", feature.getDefaultGeometry());

    }

    /**
     * This test method tests the searchTree method by providing an invalid search mode, which should throw an Exception.
     * @throws IOException if there is an error reading the file
     */
    @Test
    public void testSearchTreeWithInvalidMode() throws IOException {
        String FileName = "";
        SimpleFeatureCollection features = Main.getSimpleFeatureCollection(FileName);
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(4.353735, 50.846854));

        assertThrows(Exception.class, () -> Main.searchTree(features, point, "Invalid mode", null));
    }
}