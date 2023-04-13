package util;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Envelope;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//import org.geotools.geometry.jts.ReferencedEnvelope;

import java.util.logging.Logger;

// Manque certaine méthode voir avec l'énoncé si c'est toutes les méthodes sont cohérentes. Le PIP, je pense que c'est bon
public class Polygon {

    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(Polygon.class);
    private List<Point> points;
//    private Rectangle MBR;
//    private Envelope2D MBR;
//    private Envelope MBR;
    private ReferencedEnvelope MBR;


    public Polygon(List<Point> points) {
        logger.fine("util.Polygon()");
        logger.info("util.Polygon created");
        this.points = new ArrayList<>(points);
        this.MBR = calculateMBR();
    }

    public List<Point> getPoints() {
        logger.fine("getPoints()");
        return points;
    }

    public void setPoints(List<Point> points) {
        logger.fine("setPoints()");
        this.points = new ArrayList<>(points);
    }

    public double area() {
        logger.fine("area()");
        int n = points.size();
        double area = 0.0;
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            area += points.get(i).getX() * points.get(j).getY();
            area -= points.get(j).getX() * points.get(i).getY();
        }
        return Math.abs(area) / 2.0;
    }

//    private Rectangle calculateMBR() {
    private ReferencedEnvelope calculateMBR() {
//    private Envelope2D calculateMBR() {
//    private Envelope calculateMBR() {
        logger.fine("calculateMBR()");
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point p : points) {
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
            if (p.getX() > maxX) {
                maxX = p.getX();
            }
            if (p.getY() > maxY) {
                maxY = p.getY();
            }
        }

//        return new Rectangle((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
        return null;
    }



    public static boolean PIP(Point point, Polygon polygon) {
        logger.fine("PIP()");
        int count = 0;
        List<Point> vertices = polygon.getPoints();
        int n = vertices.size();
        Point p1 = vertices.get(0);
        for (int i = 1; i <= n; i++) {
            Point p2 = vertices.get(i % n);
            if (point.getY() > Math.min(p1.getY(), p2.getY())) { // todo if values are equal, it should not return null
                if (point.getY() <= Math.max(p1.getY(), p2.getY())) {  // todo if values are equal, it should not return null
                    if (point.getX() <= Math.max(p1.getX(), p2.getX())) {
                        if (p1.getY() != p2.getY()) {
                            double xIntersection = (point.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();
                            if (p1.getX() == p2.getX() || point.getX() <= xIntersection) {
                                count++;
                            }
                        }
                    }
                }
            }
            p1 = p2;
        }
        return count % 2 != 0;
    }

//    public Rectangle getMBR() {
    public ReferencedEnvelope getMBR() {
        logger.fine("getMBR()");
        return this.MBR;
    }
}

