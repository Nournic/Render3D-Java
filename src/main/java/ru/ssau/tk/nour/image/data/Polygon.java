package ru.ssau.tk.nour.image.data;

public class Polygon {
    private final Point3D onePoint;
    private final Point3D twoPoint;
    private final Point3D threePoint;

    public Polygon(Point3D onePoint, Point3D twoPoint, Point3D threePoint) {
        this.onePoint = onePoint;
        this.twoPoint = twoPoint;
        this.threePoint = threePoint;
    }

    public Point3D getOnePoint() {
        return onePoint;
    }

    public Point3D getTwoPoint() {
        return twoPoint;
    }

    public Point3D getThreePoint() {
        return threePoint;
    }
}
