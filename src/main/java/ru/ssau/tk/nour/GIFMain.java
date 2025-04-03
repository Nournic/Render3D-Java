package ru.ssau.tk.nour;

import ru.ssau.tk.nour.image.ModelObjectReader;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.method.ImageSceneDrawer;
import ru.ssau.tk.nour.image.other.Scene;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class GIFMain {
    private final static int width = 500;
    private final static int height = 500;
    private static int posX;
    private static int posY;
    private static Scene scene;
    private static JLabel image;

    public static void main(String[] args) throws IOException {
        File model_obj1, model_obj2;
        BufferedImage texture, texture1;
        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel(new BorderLayout());

        try {
            model_obj1 = new File(Objects.requireNonNull(Main.class.getResource("/model_1.obj")).toURI());
            model_obj2 = new File(Objects.requireNonNull(Main.class.getResource("/frog.obj")).toURI());
            texture = ImageIO.read(
                    new File(Objects.requireNonNull(Main.class.getResource("/textures/bunny-atlas.jpg")).toURI())
            );
            texture1 = ImageIO.read(
                    new File(Objects.requireNonNull(Main.class.getResource("/textures/frog_texture.jpg")).toURI())
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Missing path to obj obj");
        }

        ArrayList<Model> models = new ArrayList<>();

        // Настройка модели
        Model model = new ModelObjectReader(model_obj1).getModelBuilder()
                .setTexture(texture)
                .move(250,250,0)
                .move(-50,0,0)
                .rotate(Math.PI/4, 0, 0)
                .scale(1800)
                .build();
        models.add(model);

        // Настройка модели
        model = new ModelObjectReader(model_obj2).getModelBuilder()
                .setTexture(texture1)
                .move(250,250,0).move(150,0,0)
                .rotate(0,3*Math.PI/4,0)
                .scale(50)
                .build();
        models.add(model);

        scene = new Scene(models);
        ImageSceneDrawer drawer = new ImageSceneDrawer(scene);
        BufferedImage img = drawer.draw(width, height);

        img = createRotated(img);

        image = new JLabel(new ImageIcon(img));
        mainPanel.add(image);
        frame.add(mainPanel);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                posX = e.getX();
                posY = e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(e.getXOnScreen()-posX,e.getYOnScreen()-posY);
            }
        });
        frame.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                ArrayList<Model> models = new ArrayList<>();
                for(Model model: scene.getModels()){
                    model.rotate(0.01*notches,0,0);
                    models.add(model);
                }
                scene = new Scene(models);
                BufferedImage img = new ImageSceneDrawer(scene).draw(width,height);
                img = createRotated(img);
                image.setIcon(new ImageIcon(img));
            }
        });

        frame.setVisible(true);
    }

    private static BufferedImage createRotated(BufferedImage image)
    {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.PI, image.getWidth()/2.0, image.getHeight()/2.0);
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(
            BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
