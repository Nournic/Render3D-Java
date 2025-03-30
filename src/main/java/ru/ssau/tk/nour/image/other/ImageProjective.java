package ru.ssau.tk.nour.image.other;

import ru.ssau.tk.nour.image.data.Point3D;
import ru.ssau.tk.nour.image.data.Polygon;

import java.util.ArrayList;

public class ImageProjective implements ImageTransform{
    private final double xScale;
    private final double yScale;
    private final int centerX;
    private final int centerY;

    public ImageProjective(double xScale, double yScale, int centerX, int centerY) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public ArrayList<Polygon> transform(ArrayList<Polygon> polygons) {
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        Matrix3 transform = new Matrix3(new double[]{
                xScale, 0, centerX,
                0, yScale, centerY,
                0, 0, 1
        });

        for (Polygon plg: polygons)
            newPolygons.add(transformPolygon(plg, transform));

        return newPolygons;
    }

    private Polygon transformPolygon(Polygon plg, Matrix3 transform){
        Point3D p1 = transform.transform(plg.getOnePoint());
        Point3D p2 = transform.transform(plg.getTwoPoint());
        Point3D p3 = transform.transform(plg.getThreePoint());

        return new Polygon(p1,p2,p3);
    }
}
