public class Point {
    private double x;
    private double y;
    private double z;

    public Point(double x, double y) {
        this(x, y, 0.0);
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double distanceTo(Point other) {
        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        double dz = this.z - other.getZ();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
