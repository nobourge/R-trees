import java.util.logging.Logger;

public class Point {
    private static final Logger logger = org.geotools.util.logging.Logging.getLogger(Point.class);
    private double x;
    private double y;
    private double z;

    public Point(double x, double y) {
        this(x, y, 0.0);
        logger.fine("Point(x, y)");
    }

    public Point(double x, double y, double z) {
        logger.fine("Point(x, y, z)");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        logger.fine("getX()");
        return x;
    }

    public double getY() {
        logger.fine("getY()");
        return y;
    }

    public double getZ() {
        logger.fine("getZ()");
        return z;
    }

    public void setX(double x) {
        logger.fine("setX()");
        this.x = x;
    }

    public void setY(double y) {
        logger.fine("setY()");
        this.y = y;
    }

    public void setZ(double z) {
        logger.fine("setZ()");
        this.z = z;
    }

    public double distanceTo(Point other) {
        logger.fine("distanceTo()");
        logger.fine("Returning the distance between the two points");
        logger.fine(" this.x: " + this.x + ",  this.y: " + this.y + ",  this.z: " + this.z);
        logger.fine("other.x: " + other.getX() + ", other.y: " + other.getY() + ", other.z: " + other.getZ());

        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        double dz = this.z - other.getZ();
        logger.fine("dx: " + dx + ", dy: " + dy + ", dz: " + dz);
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    @Override
    public String toString() {
        logger.fine("toString()");
        logger.fine("Returning a string representation of the point");
        logger.fine("x: " + x + ", y: " + y + ", z: " + z);
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
