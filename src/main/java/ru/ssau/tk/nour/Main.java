package ru.ssau.tk.nour;

import ru.ssau.tk.nour.image.other.ModelRotate;
import ru.ssau.tk.nour.image.other.ImageScale;
import ru.ssau.tk.nour.image.ImageWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    private final static int width = 500;
    private final static int height = 500;

    public static void main(String[] args) throws IOException {
        File obj, obj1;
        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel(new BorderLayout());

        try {
            obj = new File(Objects.requireNonNull(Main.class.getResource("/model_1.obj")).toURI());
            obj1 =new File(Objects.requireNonNull(Main.class.getResource("/model.obj")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Missing path to obj obj");
        }

        ImageScale imageScale = new ImageScale.Builder()
                .scaleX(900).scaleY(900).shiftX(width/2.0).shiftY(height/2.0)
                .build();

        ArrayList<BufferedImage> textures = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();

        ModelRotate rotate = new ModelRotate.Builder().beta(Math.PI).build();

        BufferedImage texture = ImageIO.read(new File("C:\\Games\\bunny_texture.jpg"));
        BufferedImage texture1 = ImageIO.read(new File("C:\\Games\\model.bmp"));

        files.add(obj); files.add(obj1);
        textures.add(texture); textures.add(texture1);

        //ImageWriter writer = new ImageWriter(obj, imageScale, rotate, texture);
        ImageWriter writer = new ImageWriter(files, textures);


        BufferedImage img = writer.getImage(width, height);
        img = createRotated(img);

        JLabel image = new JLabel(new ImageIcon(img));
        mainPanel.add(image);
        frame.add(mainPanel);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ImageIO.write(img, "jpg", new File("C:\\Games\\image3.jpg"));
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