package ru.ssau.tk.nour;

import ru.ssau.tk.nour.image.ModelObjectReader;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.method.ImageSceneDrawer;
import ru.ssau.tk.nour.image.other.ProjectScale;
import ru.ssau.tk.nour.image.other.Scene;
import ru.ssau.tk.nour.image.other.Vector3;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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

        // Настройка модели: Лягушка
        Model model = new ModelObjectReader(model_obj2).getModelBuilder()
                .setTexture(texture1)
                .move(0,-1,5)
                .scale(0.7)
                .rotate(90, 0, 180)
                .build();
        models.add(model);

        // Настройка модели: Заяц
        model = new ModelObjectReader(model_obj1).getModelBuilder()
                .setTexture(texture)
                .move(1,-1, 5)
                .scale(30)
                .rotate(new Vector3(0,10,0), Math.PI)
//                .rotate(0,180,0)
                .build();
        models.add(model);

        scene = new Scene(models);
        scene.setBackgroundColor(Color.cyan);

        ProjectScale projectScale = new ProjectScale.Builder()
                .scaleX(400).scaleY(400)
                .shiftX(width/2.0).shiftY(height/2.0)
                .build();

        ImageSceneDrawer drawer = new ImageSceneDrawer(scene, projectScale);
        BufferedImage img = drawer.draw(width, height);

        AffineTransform tx=AffineTransform.getScaleInstance(1.0,-1.0);  //scaling
        tx.translate(0,-img.getHeight());  //translating
        AffineTransformOp tr = new AffineTransformOp(tx,null);  //transforming

        img = tr.filter(img, null);
        //img = createRotated(img);
        ImageIO.write(img, "jpg", new File("C:\\Games\\Models\\model.jpg"));
        image = new JLabel(new ImageIcon(img));
        mainPanel.add(image);
        frame.add(mainPanel);
        frame.setSize(width, height);
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
