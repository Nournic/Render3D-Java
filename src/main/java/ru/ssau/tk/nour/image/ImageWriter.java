package ru.ssau.tk.nour.image;

import ru.ssau.tk.nour.image.method.ImageDrawer;
import ru.ssau.tk.nour.image.other.ImageScale;
import ru.ssau.tk.nour.image.other.ImageTransform;
import ru.ssau.tk.nour.image.other.ModelRotate;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageWriter {
    private final ImageDrawer methodDraw;

    public ImageWriter(File pathToObject, ImageScale imageScale) {
        ModelObjectReader objectReader = new ModelObjectReader(pathToObject);
        methodDraw = new ImageDrawer(objectReader.getModel(), imageScale);
    }

    public ImageWriter(File pathToObject, ImageScale imageScale, ModelRotate transform, BufferedImage texture) {
        ModelObjectReader objectReader = new ModelObjectReader(pathToObject);
        methodDraw = new ImageDrawer(objectReader.getModel(), imageScale, transform, texture);
    }

    public BufferedImage getImage(int width, int height){
        return methodDraw.draw(width, height);
    }
}
