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

    private Polygon transformPolygon(Polygon plg, Matrix3 transform){
        Vector3 p1 = transform.transform(plg.getFirstVector());
        Vector3 p2 = transform.transform(plg.getSecondVector());
        Vector3 p3 = transform.transform(plg.getThirdVector());

        return new Polygon(p1,p2,p3);
    }
}
