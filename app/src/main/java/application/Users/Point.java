package application.Users;

/**
 * Created by Avi on 2018-01-20.
 */

public final class Point{
    private double x;
    private double y;

    public Point(){

    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(org.springframework.data.geo.Point point){
        this.x = point.getX();
        this.y = point.getY();

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
