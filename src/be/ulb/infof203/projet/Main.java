package be.ulb.infof203.projet;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

//    public static RTree buildRTree(SimpleFeatureCollection all_features){
//        RTree rTree = new RTree(4, 4, 4, 4);
//
//
//        try ( SimpleFeatureIterator iterator = all_features.features() ){
//            while( iterator.hasNext()){
//                SimpleFeature feature = iterator.next();
//
////                MultiPolygon polygon = (MultiPolygon) feature.getDefaultGeometry();
//                Polygon polygon = (Polygon) feature.getDefaultGeometry();
////                Node node = new Node();
//                // random String
//                String id = Objects.toString(feature.getAttribute("id"));
//                rTree.addLeaf(rTree.getRoot()
////                        , polygon.getEnvelopeInternal()
//                        , id
//                        , polygon
//                );
//            }
//        }
//        return rTree;
//    }

    public static SimpleFeatureCollection getSimpleFeatureCollection(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists())
            throw new RuntimeException("Shapefile does not exist.");

        // create a map content and add our shapefile to it
        FileDataStore store = FileDataStoreFinder.getDataStore(file); // store is a ShapefileDataStore
        //
        SimpleFeatureSource featureSource = store.getFeatureSource(); // featureSource is a ShapefileFeatureSource

        SimpleFeatureCollection all_features = featureSource.getFeatures();

        store.dispose(); // close the store

        ReferencedEnvelope global_bounds = featureSource.getBounds();
        logger.info("Global bounds: "+global_bounds);

        return all_features;
    }

    public static SimpleFeature getFeatureById(SimpleFeatureCollection collection, String id) throws IOException {
        SpatialIndexFeatureCollection indexedCollection = new SpatialIndexFeatureCollection(collection);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
        Filter filter = filterFactory.equals(filterFactory.property("ID"), filterFactory.literal(id));
        return indexedCollection.subCollection(filter).features().next();
    }
    public static Object search(SimpleFeatureCollection all_features, Point point, String mode) throws Exception {
        logger.debug("search()");

        System.out.println(all_features.size()+" features");

        if (mode.equals("iterative")) {
            logger.debug("mode: iterative");
            return searchIterative(all_features, point);
        } else {
            return searchTree(all_features, point, mode);
        }
    }

    private static SimpleFeature searchTree(SimpleFeatureCollection allFeatures
            , Point point
            , String mode) throws Exception {
        logger.debug("searchTree()");
        // chrono start:
        long start = System.currentTimeMillis();
        SimpleFeature target;
        RTree rTree = new RTree(4, 4, 4, 4);

        rTree.addFeatureCollection(allFeatures
        , mode);

        target = getFeatureById(allFeatures, rTree.search(point).get(0).getLabel());
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
                    // chrono stop:
                    long stop = System.currentTimeMillis();
                    System.out.println("Time: "+(stop-start)+" ms");
                }
            }
        }
        return target;
    }

    public static void main(String[] args) throws Exception {
        logger.debug("main()");
        String filename ="resources/sh_statbel_statistical_sectors_20210101/sh_statbel_statistical_sectors_20210101.shp";
        // iterative search: 165 ms
        // rtree search:
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

        GeometryBuilder gb = new GeometryBuilder();
        Point p = gb.point(152183, 167679);// Plaine
        logger.debug("util.Point: "+p);

        //util.Point p = gb.point(4.4, 50.8);//
        //util.Point p = gb.point(58.0, 47.0);
        //util.Point p = gb.point(10.6,59.9);// Oslo

        //util.Point p = gb.point(-70.9,-33.4);// Santiago
        //util.Point p = gb.point(169.2, -52.5);//NZ

        //util.Point p = gb.point(172.97365198326708, 1.8869725782923172);

//        util.Point p = gb.point(r.nextInt((int) global_bounds.getMinX()
//                                   , (int) global_bounds.getMaxX())
//                         , r.nextInt((int) global_bounds.getMinY()
//                                    , (int) global_bounds.getMaxY()));

//        String mode = "iterative";
        String mode = "quadratic";
//        String mode = "linear";

        SimpleFeature target = (SimpleFeature) search(getSimpleFeatureCollection(filename), p, mode);

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
