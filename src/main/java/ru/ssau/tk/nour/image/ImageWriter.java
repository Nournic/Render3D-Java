package ru.ssau.tk.nour.image;

import ru.ssau.tk.nour.image.method.ImageDrawTriangle;
import ru.ssau.tk.nour.image.method.ImageDrawer;
import ru.ssau.tk.nour.image.other.ImageScale;
import ru.ssau.tk.nour.image.other.ImageTransform;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageWriter {
    private final ImageDrawer methodDraw;

    public ImageWriter(File pathToObject, ImageScale imageScale) {
        ModelObjectReader objectReader = new ModelObjectReader(pathToObject);
        methodDraw = new ImageDrawTriangle(objectReader.getPolygons(), imageScale);
    }

    public ImageWriter(File pathToObject, ImageScale imageScale, ImageTransform transform) {
        ModelObjectReader objectReader = new ModelObjectReader(pathToObject);
        methodDraw = new ImageDrawTriangle(objectReader.getPolygons(), imageScale, transform);
    }

    public BufferedImage getImage(int width, int height){
        return methodDraw.draw(width, height);
    }
}
