/*
 *    Annexe pour l'énoncé du projet d'INFOF203 (2022-2023)
 *
 */

package be.ulb.infof203.projet;

import java.awt.Color;
import java.io.File;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.MultiPolygon;
import org.geotools.geometry.jts.ReferencedEnvelope;


import org.geotools.geometry.jts.GeometryBuilder;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Objects;

import org.geotools.swing.JMapFrame;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a map frame.
 *
 * <p>This is the GeoTools Quickstart application used in documentation a and tutorials. *
 */
public class SinglePoint {
    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile and displays its
     * contents on the screen in a map frame
     */
//    private static final Log LOGGER = org.geotools.util.logging.Logging.getLogger(SinglePoint.class);
//    private static final Logger LOGGER = org.geotools.util.logging.LogbackLoggerFactory(SinglePoint.class);
    private static final Logger logger = LoggerFactory.getLogger(SinglePoint.class);

     public static void main(String[] args) throws Exception {
         logger.debug("main()");

        // display a data store file chooser dialog for shapefiles
//        String filename="src/be/ulb/infof203/projet/WB_countries_Admin0_10m.shp";
        String filename ="resources/sh_statbel_statistical_sectors_20210101/sh_statbel_statistical_sectors_20210101.shp";
        //String filename="../projetinfof203/data/communes-20220101-shp/communes-20220101.shp";

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

//        Random r = new Random();

        GeometryBuilder gb = new GeometryBuilder();
        logger.info("Global bounds: "+global_bounds);
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

        SimpleFeature target=null;

        System.out.println(all_features.size()+" features");

        try ( SimpleFeatureIterator iterator = all_features.features() ){
            while( iterator.hasNext()){
                SimpleFeature feature = iterator.next();

                MultiPolygon polygon = (MultiPolygon) feature.getDefaultGeometry();

                if (polygon != null && polygon.contains(p)) {
                    target = feature;
                    break;
                }
            }
        }

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

