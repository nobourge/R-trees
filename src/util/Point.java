package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Point {

    private static final Logger logger = LoggerFactory.getLogger(Point.class);
    private double x;
    private double y;
    private double z;

    public Point(double x, double y) {
        this(x, y, 0.0);
        logger.debug("util.Point(x, y)");
    }

    public Point(double x, double y, double z) {
        logger.debug("util.Point(x, y, z)");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        logger.debug("getX()");
        return x;
    }

    public double getY() {
        logger.debug("getY()");
        return y;
    }

    public double getZ() {
        logger.debug("getZ()");
        return z;
    }

    public void setX(double x) {
        logger.debug("setX()");
        this.x = x;
    }

    public void setY(double y) {
        logger.debug("setY()");
        this.y = y;
    }

    public void setZ(double z) {
        logger.debug("setZ()");
        this.z = z;
    }

    public double distanceTo(Point other) {
        logger.debug("distanceTo()");
        logger.debug("Returning the distance between the two points");
        logger.debug(" this.x: " + this.x + ",  this.y: " + this.y + ",  this.z: " + this.z);
        logger.debug("other.x: " + other.getX() + ", other.y: " + other.getY() + ", other.z: " + other.getZ());

        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        double dz = this.z - other.getZ();
        logger.debug("dx: " + dx + ", dy: " + dy + ", dz: " + dz);
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    @Override
    public String toString() {
        logger.debug("toString()");
        logger.debug("Returning a string representation of the point");
        logger.debug("x: " + x + ", y: " + y + ", z: " + z);
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
