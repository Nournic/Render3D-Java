package ru.ssau.tk.nour.image.method;

import ru.ssau.tk.nour.image.data.Face;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.data.Polygon;
import ru.ssau.tk.nour.image.other.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.lang.Math.*;
import static java.lang.Math.ceil;

public class ImageDrawer {
    private final Model model;
    private final ImageScale imageScale;
    private ModelRotate rotate;
    private BufferedImage img;

    public ImageDrawer(Model model, ImageScale imageScale) {
        this.model = model;
        this.imageScale = imageScale;
    }

    public ImageDrawer(Model model, ImageScale imageScale, ModelRotate transform) {
        this(model, imageScale);

        this.rotate = transform;
    }

    public BufferedImage draw(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double[][] zBuffer = new double[1000][1000];
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                zBuffer[i][j] = Double.MAX_VALUE;
            }
        }

        ImageShift shift = new ImageShift(0, -0.04, 0.2);

//        Vector3 pivotMoveToCenter = new Vector3(width/2.0, height/2.0, 0);
//        model.move(pivotMoveToCenter);

//        if(rotate != null)
//            model.rotate(
//                    rotate.getAlpha(),
//                    rotate.getBeta(),
//                    rotate.getGamma()
//            );

        model.move(new Vector3(
                shift.getShiftX(),
                shift.getShiftY(),
                shift.getShiftZ()
        ));


        //Model newModel = scalePolygons(model);
        Graphics2D graphics = (Graphics2D) img.getGraphics();

        ArrayList<Face> globalFaces = model.getGlobalFaces();
        for (Face face : globalFaces) {
            Polygon plg = face.getPlg();
            Face projectFace = new Face(new Polygon(
                    new Vector3(
                            imageScale.getScaleX() * (plg.getFirstVector().getX() / plg.getFirstVector().getZ()) + imageScale.getShiftX(),
                            imageScale.getScaleY() * (plg.getFirstVector().getY() / plg.getFirstVector().getZ()) + imageScale.getShiftY(),
                            imageScale.getScaleZ() * plg.getFirstVector().getZ() + imageScale.getShiftZ()
                    ),
                    new Vector3(
                            imageScale.getScaleX() * (plg.getSecondVector().getX() / plg.getSecondVector().getZ()) + imageScale.getShiftX(),
                            imageScale.getScaleY() * (plg.getSecondVector().getY() / plg.getSecondVector().getZ()) + imageScale.getShiftY(),
                            imageScale.getScaleZ() * plg.getSecondVector().getZ() + imageScale.getShiftZ()
                    ),
                    new Vector3(
                            imageScale.getScaleX() * (plg.getThirdVector().getX() / plg.getThirdVector().getZ()) + imageScale.getShiftX(),
                            imageScale.getScaleY() * (plg.getThirdVector().getY() / plg.getThirdVector().getZ()) + imageScale.getShiftY(),
                            imageScale.getScaleZ() * plg.getThirdVector().getZ() + imageScale.getShiftZ()
                    )));

            projectFace.addNorm(projectFace.getPlg().getFirstVector(), face.getNorm(plg.getFirstVector()));
            projectFace.addNorm(projectFace.getPlg().getSecondVector(), face.getNorm(plg.getSecondVector()));
            projectFace.addNorm(projectFace.getPlg().getThirdVector(), face.getNorm(plg.getThirdVector()));


            drawTriangle(graphics, zBuffer, projectFace);
        }
        graphics.dispose();

        return img;
    }

    private Model scalePolygons(Model model){
        ArrayList<Face> newFaces = new ArrayList<>();

        Model newModel = model;
//        for (Polygon plg: polygons) {
//            Vector3 v1 = new Vector3(
//                    imageScale.getScaleX() * (plg.getFirstVector().getX() / plg.getFirstVector().getZ()) + imageScale.getShiftX(),
//                    imageScale.getScaleY() * (plg.getFirstVector().getY() / plg.getFirstVector().getZ()) + imageScale.getShiftY(),
//                    imageScale.getScaleZ() * plg.getFirstVector().getZ() + imageScale.getShiftZ()
//            );
//
//            Vector3 v2 = new Vector3(
//                    imageScale.getScaleX() * (plg.getSecondVector().getX() / plg.getSecondVector().getZ()) + imageScale.getShiftX(),
//                    imageScale.getScaleY() * (plg.getSecondVector().getY() / plg.getSecondVector().getZ()) + imageScale.getShiftY(),
//                    imageScale.getScaleZ() * plg.getSecondVector().getZ() + imageScale.getShiftZ()
//            );
//
//            Vector3 v3 = new Vector3(
//                    imageScale.getScaleX() * (plg.getThirdVector().getX() / plg.getThirdVector().getZ()) + imageScale.getShiftX(),
//                    imageScale.getScaleY() * (plg.getThirdVector().getY() / plg.getThirdVector().getZ()) + imageScale.getShiftY(),
//                    imageScale.getScaleZ() * plg.getThirdVector().getZ() + imageScale.getShiftZ()
//            );
//
//            newPolygons.add(new Polygon(v1,v2,v3));
//        }

        return newModel;
    }

    private boolean drawTriangle(Graphics2D graphic, double[][] zBuffer, Face face){
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

        Vector3 lightDirection = new Vector3(0,0,1);

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
                    if(zBuffer[i][j]<zb)
                        continue;

                    Vector3 n1 = face.getNorm(face.getPlg().getFirstVector());
                    Vector3 n2 = face.getNorm(face.getPlg().getSecondVector());
                    Vector3 n3 = face.getNorm(face.getPlg().getThirdVector());

                    Vector3 normal = n1.mult(l1)
                            .add(n2.mult(l2))
                            .add(n3.mult(l3));

                    double cos_angle = normal.scalar(lightDirection)/(normal.length() * lightDirection.length());

                    double intense = max(0,-255*cos_angle);
                    Color newColor = new Color(abs((int) (intense)), abs((int) (intense)), abs((int) (intense)));

                    graphic.setColor(newColor);
                    graphic.drawRect(i,j, 1,1);
                    zBuffer[i][j]=zb;

                }
            }
        }
        return true;
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

    private static double getVectorMult(double x0, double y0, double z0, double x1, double y1, double z1){
        return x0*x1+y0*y1+z0*z1;
    }

    private static double getNorm(double x0, double y0, double z0){
        return Math.sqrt(Math.pow(x0,2)+Math.pow(y0,2)+Math.pow(z0,2));
    }
}
