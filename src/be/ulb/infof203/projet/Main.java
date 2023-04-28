package be.ulb.infof203.projet;

import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static SimpleFeatureCollection getSimpleFeatureCollection(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists())
            throw new RuntimeException("Shapefile does not exist.");

        // create a map content and add our shapefile to it
        FileDataStore store = FileDataStoreFinder.getDataStore(file); // store is a ShapefileDataStore
        SimpleFeatureSource featureSource = store.getFeatureSource(); // featureSource is a ShapefileFeatureSource
        SimpleFeatureCollection all_features = featureSource.getFeatures();
        store.dispose(); // close the store

        ReferencedEnvelope global_bounds = featureSource.getBounds();
        logger.info("Global bounds: "+global_bounds);

        return all_features;
    }

    public static int getFeaturePreviousIndex(String id) {
        String featureId = id;
        int startIndex = featureId.lastIndexOf(".") + 1; // Add 1 to exclude the dot
        int endIndex = featureId.length();
        int index = Integer.parseInt(featureId.substring(startIndex, endIndex));
        System.out.println(index);
        return index-1;
    }
    public static SimpleFeature getFeatureById(SimpleFeatureCollection collection, String id) throws IOException {
        SimpleFeatureIterator iterator = collection.features();
        SimpleFeature feature = null;
        int index = getFeaturePreviousIndex(id);
        try {
            int count = 0;
            while (count < index && iterator.hasNext()) {
                iterator.next();
                count++;
            }

            while (iterator.hasNext()) {
                feature = iterator.next();
                System.out.println(feature.getID());
                if (feature.getID().equals(id)) {
                    break;
                }
            }
        } finally {
            iterator.close();
        }
        logger.debug("feature: " + feature.getID());
        return feature;
    }
    public static Object search(SimpleFeatureCollection all_features, Point point, String mode, SimpleFeatureSource featureSource) throws Exception {
        logger.debug("search()");

        System.out.println(all_features.size()+" features");

        if (mode.equals("iterative")) {
            logger.debug("mode: iterative");
            return searchIterative(all_features, point);
        } else {
            return searchTree(all_features, point, mode, featureSource );
        }
    }

    public static SimpleFeature searchTree(SimpleFeatureCollection allFeatures
            , Point point
            , String mode
            , SimpleFeatureSource featureSource) throws Exception {
        logger.debug("searchTree()");
        // chrono start:
        long start = System.currentTimeMillis();
        SimpleFeature target;
        RTree rTree = new RTree(4, 4, 4, 4);
        rTree.addFeatureCollection(allFeatures
            , mode
            , featureSource);
        RLeaf targetLeaf = rTree.search(point).get(0);
        String label = targetLeaf.getLabel();
        logger.info("label: "+label);

        target = getFeatureById(allFeatures, label);
        // chrono stop:
        long stop = System.currentTimeMillis();
        System.out.println("Time: "+(stop-start)+" ms");
        return target;
    }

    public static SimpleFeature searchIterative(SimpleFeatureCollection all_features, Point point){
        logger.debug("searchIterative()");
        // chrono start:
        long start = System.currentTimeMillis();
        SimpleFeature target=null;
        try ( SimpleFeatureIterator iterator = all_features.features() ){
            while( iterator.hasNext()){
                SimpleFeature feature = iterator.next();
                MultiPolygon polygon = (MultiPolygon) feature.getDefaultGeometry();
                if (polygon != null && polygon.contains(point)) {
                    target = feature;
                    String id = Objects.toString(feature.getID());
                    logger.info("label: "+id);
                    // chrono stop:
                    long stop = System.currentTimeMillis();
                    System.out.println("Time: "+(stop-start)+" ms");
                }
            }
        }
        return target;
    }
    public static void maintest(String[] args) throws IOException {
        RTree rTree = new RTree(10, 5, 10, 5);

        // Premier fichier de test
        SimpleFeatureCollection mondialFeatures = RTree.getSimpleFeatureCollection(FileConst.STATBEL);
        rTree.addFeatureCollection(mondialFeatures, "quadratic", (SimpleFeatureSource) mondialFeatures.getSchema().getName());
        rTree.addFeatureCollection(mondialFeatures, "linear", (SimpleFeatureSource) mondialFeatures.getSchema().getName());
        SimpleFeatureSource featureSource = DataUtilities.source(mondialFeatures);

//        // Deuxième fichier de test
        SimpleFeatureCollection batiFeatures = RTree.getSimpleFeatureCollection(FileConst.WB_COUNTRIES);
        featureSource = DataUtilities.source(batiFeatures);
        rTree.addFeatureCollection(mondialFeatures, "linear", featureSource);
        //        rTree.addFeatureCollection(batiFeatures, "quadratic", (SimpleFeatureSource) batiFeatures.getSchema().getName());
//        rTree.addFeatureCollection(batiFeatures, "linear", (SimpleFeatureSource) batiFeatures.getSchema().getName());
//
//        // Troisième fichier de test
//        SimpleFeatureCollection coursDeauFeatures = RTree.getSimpleFeatureCollection("file.shp");
//        rTree.addFeatureCollection(coursDeauFeatures, "quadratic", (SimpleFeatureSource) coursDeauFeatures.getSchema().getName());
//        rTree.addFeatureCollection(coursDeauFeatures, "linear", (SimpleFeatureSource) coursDeauFeatures.getSchema().getName());

        // Perform search tests
        long startTime = System.currentTimeMillis();

        CoordinateSequence coordinates = new CoordinateArraySequence(new Coordinate[]{new Coordinate(0, 0)});

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        Point point = new Point(coordinates, geometryFactory);

//        List<RLeaf> searchResult = rTree.search(point);
//        System.out.println(searchResult.toString());
        long endTime = System.currentTimeMillis();
        long searchTime = endTime - startTime;
        System.out.println("Search time: " + searchTime + " ms");
    }
    public static void main(String[] args) throws Exception {
//        maintest(args);
        logger.debug("main()");
        GeometryBuilder gb = new GeometryBuilder();
        Point p = PointConst.STATBEL;
        String filename = FileConst.STATBEL;
//        String filename = FileConst.REGIONS;
        logger.debug("util.Point: "+p);
        File file = new File(filename);
        if (!file.exists())
            throw new RuntimeException("Shapefile does not exist.");
        // create a map content and add our shapefile to it
        FileDataStore store = FileDataStoreFinder.getDataStore(file); // store is a ShapefileDataStore
        //
        SimpleFeatureSource featureSource = store.getFeatureSource(); // featureSource is a ShapefileFeatureSource

        SimpleFeatureCollection all_features=featureSource.getFeatures();

        store.dispose();

        ReferencedEnvelope global_bounds = featureSource.getBounds();
        logger.info("Global bounds: "+global_bounds);

//        String mode = "iterative";
//        String mode = "quadratic";
        String mode = "linear";
        SimpleFeature target = (SimpleFeature) search(getSimpleFeatureCollection(filename)
                                                    , p
                                                    , mode
                                                    , featureSource);
        if (target == null)
            System.out.println("util.Point not in any polygon!");

        else {
            for(Property prop: target.getProperties()) {
                if (!Objects.equals(prop.getName().toString(), "the_geom")) {
                    System.out.println(prop.getName()+": "+prop.getValue());
                }
            }
        }
        MapContent map = new MapContent();
        map.setTitle("Projet INFO-F203");

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);

        ListFeatureCollection collection = new ListFeatureCollection(featureSource.getSchema());
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureSource.getSchema());


        // Add target polygon
        collection.add(target);

        // Add util.Point
        Polygon c= gb.circle(p.getX(), p.getY(), all_features.getBounds().getWidth()/200,10);
        featureBuilder.add(c);
        collection.add(featureBuilder.buildFeature(null));

        // Add MBR
        if (target !=null) {
            featureBuilder.add(gb.box(target.getBounds().getMinX(),
                    target.getBounds().getMinY(),
                    target.getBounds().getMaxX(),
                    target.getBounds().getMaxY()
            ));

            //collection.add(featureBuilder.buildFeature(null));

            collection.add(featureBuilder.buildFeature(null));
        }

        Style style2 = SLD.createLineStyle(Color.red, 2.0f);
        Layer layer2 = new FeatureLayer(collection, style2);
        map.addLayer(layer2);

        // Now display the map
        JMapFrame.showMap(map);
    }
}
