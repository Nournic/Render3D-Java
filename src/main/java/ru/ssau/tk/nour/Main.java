package ru.ssau.tk.nour;

import ru.ssau.tk.nour.image.other.ImageProjective;
import ru.ssau.tk.nour.image.other.ImageRotate;
import ru.ssau.tk.nour.image.other.ImageScale;
import ru.ssau.tk.nour.image.ImageWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Objects;

public class Main {
    private final static int width = 1000;
    private final static int height = 1000;

    public static void main(String[] args) throws IOException {
        File file, out;

        try {
            file = new File(Objects.requireNonNull(Main.class.getResource("/model_1.obj")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Missing path to obj file");
        }

        ImageScale imageScale = new ImageScale.Builder()
                .scaleX(1800).scaleY(1800).shiftX(width/2.0).shiftY(height/2.0)
                .build();

        ImageRotate rotate = new ImageRotate.Builder()
                .beta(Math.PI/4).alpha(0)
                .build();

        ImageWriter writer = new ImageWriter(file, imageScale, rotate);

        BufferedImage img = writer.getImage(1000,1000);
        img = createRotated(img);

        out = new File("C:\\Games\\image1.jpg");

        ImageIO.write(img, "jpg", out);
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