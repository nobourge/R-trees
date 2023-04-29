package be.ulb.infof203.projet;

import org.geotools.geometry.jts.GeometryBuilder;
import org.locationtech.jts.geom.Point;

public class PointConst {

    static GeometryBuilder gb = new GeometryBuilder();

//  Plaine

    public static final Point STATBEL = gb.point(152183, 167679);// Plaine;
    public static final Point WB_COUNTRIES = gb.point(4.4, 50.8);// Belgium;

//    Point p = gb.point(4.4, 50.8);// Belgium

    //Point p = gb.point(58.0, 47.0);// Kazakhstan


    //util.Point p = gb.point(10.6,59.9);// Oslo

    //util.Point p = gb.point(-70.9,-33.4);// Santiago
    //util.Point p = gb.point(169.2, -52.5);//NZ

    //util.Point p = gb.point(172.97365198326708, 1.8869725782923172);

//        util.Point p = gb.point(r.nextInt((int) global_bounds.getMinX()
//                                   , (int) global_bounds.getMaxX())
//                         , r.nextInt((int) global_bounds.getMinY()
//                                    , (int) global_bounds.getMaxY()));
}
