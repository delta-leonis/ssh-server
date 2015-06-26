package nl.saxion.robosim.model;

/**
 * Created by Kris on 17-6-2015.
 */
public class Target {
    private final double x, y;

    public Target(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
