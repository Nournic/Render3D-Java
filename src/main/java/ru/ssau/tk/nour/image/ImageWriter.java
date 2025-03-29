package ru.ssau.tk.nour.image;

import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.method.Drawer;
import ru.ssau.tk.nour.image.method.ImageDrawer;
import ru.ssau.tk.nour.image.method.ImageSceneDrawer;
import ru.ssau.tk.nour.image.other.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageWriter {
    private final Drawer methodDraw;

    public ImageWriter(File pathToObject, ImageScale imageScale) {
        ModelObjectReader objectReader = new ModelObjectReader(pathToObject);
        methodDraw = new ImageDrawer(objectReader.getModel(), imageScale);
    }

    public ImageWriter(File pathToObject, ImageScale imageScale, ModelRotate transform, BufferedImage texture) {
        ModelObjectReader objectReader = new ModelObjectReader(pathToObject);
        Model model = objectReader.getModel();
        model.setTexture(texture);
        methodDraw = new ImageDrawer(model, imageScale, transform);
    }

    public ImageWriter(ArrayList<File> pathsToObjects, ArrayList<BufferedImage> textures){
        ArrayList<Model> models = new ArrayList<>();
        for(int i = 0; i < pathsToObjects.size(); i++) {
            models.add(new ModelObjectReader(pathsToObjects.get(i)).getModel());
            models.getLast().setTexture(textures.get(i));
        }

        methodDraw = new ImageSceneDrawer(new Scene(models));
    }

    public BufferedImage getImage(int width, int height){
        return methodDraw.draw(width, height);
    }
}
