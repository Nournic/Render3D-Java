package ru.ssau.tk.nour.image.other;

import ru.ssau.tk.nour.image.data.Point3D;
import ru.ssau.tk.nour.image.data.Polygon;

import java.util.ArrayList;

public class ImageShift implements ImageTransform{
    private final double shiftX;
    private final double shiftY;
    private final double shiftZ;

    public ImageShift(double shiftX, double shiftY, double shiftZ) {
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.shiftZ = shiftZ;
    }

    @Override
    public ArrayList<Polygon> transform(ArrayList<Polygon> polygons) {
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (Polygon plg: polygons)
            newPolygons.add(transformPolygon(plg));

        return newPolygons;
    }

    private Polygon transformPolygon(Polygon plg){
        Point3D p1 = plg.getOnePoint();
        p1.setX(p1.getX() + shiftX);
        p1.setY(p1.getY() + shiftY);
        p1.setZ(p1.getZ() + shiftZ);

        Point3D p2 = plg.getTwoPoint();
        p2.setX(p2.getX() + shiftX);
        p2.setY(p2.getY() + shiftY);
        p2.setZ(p2.getZ() + shiftZ);

        Point3D p3 = plg.getThreePoint();
        p3.setX(p3.getX() + shiftX);
        p3.setY(p3.getY() + shiftY);
        p3.setZ(p3.getZ() + shiftZ);

        return new Polygon(p1,p2,p3);
    }
}
