package ru.ssau.tk.nour.image.method;

import ru.ssau.tk.nour.image.data.Face;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.data.Polygon;
import ru.ssau.tk.nour.image.other.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.*;

public class ImageSceneDrawer implements Drawer {
    private final Scene scene;
    private final ProjectScale projectScale;
    private BufferedImage img;
    private static final Vector3 lightDirection = new Vector3(0,0,1);

    private int width;
    private int height;

    public ImageSceneDrawer(Scene scene) {
        this.scene = scene;
        this.projectScale = new ProjectScale.Builder().build();
    }

    public ImageSceneDrawer(Scene scene, ProjectScale projectScale) {
        this.scene = scene;
        this.projectScale = projectScale;
    }

    public BufferedImage draw(int width, int height) {
        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double[] zBuffer = new double[width*height];
        Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);

        Graphics2D graphics = (Graphics2D) img.getGraphics();
        graphics.setColor(Objects.requireNonNullElse(scene.getBackgroundColor(), Color.BLACK));
        for (int i = 0; i < img.getHeight(); i++) {
            graphics.drawRect(0,0, img.getWidth(), i);
        }

        for(Model model: scene.getModels()) {
            ArrayList<Face> globalFaces = model.getGlobalFaces();
            for (Face face : globalFaces) {
                Face projectFace;
                if(projectScale != null)
                    projectFace = projectFace(face);
                else
                    projectFace = face;
                drawTriangle(graphics, zBuffer, projectFace, model.getTexture());
            }
        }
        graphics.dispose();
        return img;
    }

    private Face projectFace(Face oldFace){
        Polygon plg = oldFace.getPlg();
        Face projectFace = new Face(new Polygon(
                new Vector3(
                        projectScale.getScaleX() * (plg.getFirstVector().getX() / plg.getFirstVector().getZ()) + projectScale.getShiftX(),
                        projectScale.getScaleY() * (plg.getFirstVector().getY() / plg.getFirstVector().getZ()) + projectScale.getShiftY(),
                        projectScale.getScaleZ() * plg.getFirstVector().getZ() + projectScale.getShiftZ()
                ),
                new Vector3(
                        projectScale.getScaleX() * (plg.getSecondVector().getX() / plg.getSecondVector().getZ()) + projectScale.getShiftX(),
                        projectScale.getScaleY() * (plg.getSecondVector().getY() / plg.getSecondVector().getZ()) + projectScale.getShiftY(),
                        projectScale.getScaleZ() * plg.getSecondVector().getZ() + projectScale.getShiftZ()
                ),
                new Vector3(
                        projectScale.getScaleX() * (plg.getThirdVector().getX() / plg.getThirdVector().getZ()) + projectScale.getShiftX(),
                        projectScale.getScaleY() * (plg.getThirdVector().getY() / plg.getThirdVector().getZ()) + projectScale.getShiftY(),
                        projectScale.getScaleZ() * plg.getThirdVector().getZ() + projectScale.getShiftZ()
                )));

        projectFace.addNorm(projectFace.getPlg().getFirstVector(), oldFace.getNorm(plg.getFirstVector()));
        projectFace.addNorm(projectFace.getPlg().getSecondVector(), oldFace.getNorm(plg.getSecondVector()));
        projectFace.addNorm(projectFace.getPlg().getThirdVector(), oldFace.getNorm(plg.getThirdVector()));

        projectFace.addTexture(projectFace.getPlg().getFirstVector(), oldFace.getTexture(plg.getFirstVector()));
        projectFace.addTexture(projectFace.getPlg().getSecondVector(), oldFace.getTexture(plg.getSecondVector()));
        projectFace.addTexture(projectFace.getPlg().getThirdVector(), oldFace.getTexture(plg.getThirdVector()));

        return projectFace;
    }

    private void drawTriangle(Graphics2D graphic, double[] zBuffer, Face face, BufferedImage texture){
        Polygon plg = face.getPlg();

        Vector3 p1 = plg.getFirstVector();
        Vector3 p2 = plg.getSecondVector();
        Vector3 p3 = plg.getThirdVector();

        double xmin = min(min(p1.getX(), p2.getX()), p3.getX());
        double xmax = max(max(p1.getX(), p2.getX()), p3.getX());
        double ymin = min(min(p1.getY(), p2.getY()), p3.getY());
        double ymax = max(max(p1.getY(), p2.getY()), p3.getY());

        xmin = xmin < 0 ? 0 : xmin;
        xmax = xmax > img.getWidth() ? img.getWidth() : xmax;
        ymin = ymin < 0 ? 0 : ymin;
        ymax = ymax > img.getHeight() ? img.getHeight() : ymax;


        for (int i = (int)floor(xmin); i < (int)ceil(xmax); i++) {
            for (int j = (int)floor(ymin); j < (int)ceil(ymax); j++) {
                ArrayList<Double> barycentric = evaluateBarycentricCoordinates(i,j,p1.getX(),p1.getY(),p2.getX(),p2.getY(),p3.getX(),p3.getY());
                Double l1 = barycentric.getFirst();
                Double l2 = barycentric.get(1);
                Double l3 = barycentric.getLast();

                boolean paint = true;
                for (Double l: barycentric){
                    if (Double.compare(l, 0.0) < 0) {
                        paint = false;
                        break;
                    }
                }
                if(paint){
                    double zb = p1.getZ()*l1 + p2.getZ()*l2+p3.getZ()*l3;
                    if(zBuffer[img.getWidth()*i+j]<zb)
                        continue;

                    Vector3 n1 = face.getNorm(p1);
                    Vector3 n2 = face.getNorm(p2);
                    Vector3 n3 = face.getNorm(p3);

                    Vector3 normal = n1.mult(l1)
                            .add(n2.mult(l2))
                            .add(n3.mult(l3));

                    double cos_angle = normal.scalar(lightDirection)/(normal.length() * lightDirection.length());

                    double intense = abs(-255*cos_angle);
                    Color newColor = new Color(abs((int) (intense)), abs((int) (intense)), abs((int) (intense)));

                    if(texture!=null){
                        Vector3 vt1 = face.getTexture(p1);
                        Vector3 vt2 = face.getTexture(p2);
                        Vector3 vt3 = face.getTexture(p3);
                        int xt = (int)(texture.getWidth() * (l1 * vt1.getX() + l2 * vt2.getX() + l3 * vt3.getX()));
                        int yt = (int)(texture.getHeight() * (l1 * vt1.getY() + l2 * vt2.getY() + l3 * vt3.getY()));
                        newColor = new Color(texture.getRGB(xt,yt));

                        int red = (int)abs(max(0.0, -newColor.getRed() * cos_angle));
                        int green = (int)abs(max(0.0, -newColor.getGreen() * cos_angle));
                        int blue = (int)abs(max(0.0, -newColor.getBlue() * cos_angle));
                        newColor = new Color(red, green, blue);
                    }

                    graphic.setColor(newColor);
                    graphic.drawRect(i,j, 1,1);
                    zBuffer[img.getWidth()*i+j]=zb;

                }
            }
        }
    }

    private static ArrayList<Double> evaluateBarycentricCoordinates(int x, int y, double x0, double y0, double x1, double y1, double x2, double y2){
        double v = (x0 - x2) * (y1 - y2) - (x1 - x2) * (y0 - y2);

        double lambda0 = ((x-x2)*(y1-y2)-(x1-x2)*(y-y2))/ v;
        double lambda1 = ((x0 - x2) * (y - y2) - (x - x2) * (y0 - y2)) / v;
        double lambda2 = 1.0 - lambda0 - lambda1;

        ArrayList<Double> result = new ArrayList<>();
        result.add(lambda0);
        result.add(lambda1);
        result.add(lambda2);

        return result;
    }
}
