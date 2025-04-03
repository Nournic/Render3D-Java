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
    private final static int width = 1000;
    private final static int height = 1000;

    public static void main(String[] args) throws IOException {
        File obj;
        BufferedImage texture;
        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel(new BorderLayout());

        try {
            obj = new File(Objects.requireNonNull(Main.class.getResource("/frog.obj")).toURI());
            texture = ImageIO.read(
                    new File(Objects.requireNonNull(Main.class.getResource("/textures/frog_texture.jpg")).toURI())
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error in loading files of models or textures");
        }

        ImageScale imageScale = new ImageScale.Builder()
                .scaleX(400).scaleY(400).shiftX(width/2.0).shiftY(height/2.0)
                .build();

        ModelRotate rotate = new ModelRotate.Builder().beta(Math.PI).alpha(-Math.PI/2 + Math.PI/5).build();
        ImageWriter writer = new ImageWriter(obj, imageScale, rotate, texture);

        BufferedImage img = writer.getImage(width, height);
        img = createRotated(img);

        ImageIO.write(img, "jpg", new File("C:\\Games\\Models\\model.jpg"));

        JLabel image = new JLabel(new ImageIcon(img));
        mainPanel.add(image);
        frame.add(mainPanel);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);
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