package ru.ssau.tk.nour.image.other;

import lombok.Data;
import ru.ssau.tk.nour.image.data.Polygon;

@Data
public class ImageShift implements ImageTransform{
    private final double shiftX;
    private final double shiftY;
    private final double shiftZ;

    public ImageShift(double shiftX, double shiftY, double shiftZ) {
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.shiftZ = shiftZ;
    }

    private Polygon transformPolygon(Polygon plg){
        Vector3 p1 = plg.getFirstVector().add(new Vector3(shiftX, shiftY, shiftZ));
        Vector3 p2 = plg.getSecondVector().add(new Vector3(shiftX, shiftY, shiftZ));
        Vector3 p3 = plg.getThirdVector().add(new Vector3(shiftX, shiftY, shiftZ));

        return new Polygon(p1,p2,p3);
    }
}
