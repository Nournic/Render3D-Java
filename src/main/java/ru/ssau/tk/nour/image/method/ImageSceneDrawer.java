package ru.ssau.tk.nour.image.method;

import ru.ssau.tk.nour.image.data.Face;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.data.Polygon;
import ru.ssau.tk.nour.image.other.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.lang.Math.*;

public class ImageSceneDrawer implements Drawer {
    private final Scene scene;
    private BufferedImage img;

    private int width;
    private int height;

    public ImageSceneDrawer(Scene scene) {
        this.scene = scene;
    }

    public BufferedImage draw(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double[][] zBuffer = new double[1000][1000];
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                zBuffer[i][j] = Double.MAX_VALUE;
            }
        }

        Graphics2D graphics = (Graphics2D) img.getGraphics();
        graphics.setColor(Color.cyan);
        for (int i = 0; i < img.getHeight(); i++)
            graphics.drawRect(0,0, img.getWidth(), i);

        //int i = 1;
        //double n = 2*Math.PI/scene.getModels().size();
        //double nt = 0;
        Vector3 mov = new Vector3(125, 0, 0);
        for(int i = 0; i < 2; i++){
            Model model = scene.getModels().get(i);
            if(i == 1)
                model.scale(100);
            else {model.scale(1800);model.move(new Vector3(0,-100,0));}

            model.move(new Vector3(250,250,0));
            model.rotate(0, Math.PI,0);
            model.move(mov);
            mov = mov.mult(-1);


            //  model.move(new Vector3(125*Math.sin(nt), 125*Math.cos(nt), 0));
          //  model.rotate(0,0, -2*Math.PI + nt);
           // nt+=n;
            ArrayList<Face> globalFaces = model.getGlobalFaces();
            for (Face face : globalFaces) {
                drawTriangle(graphics, zBuffer, face, model.getTexture());
            }
        }
        graphics.dispose();

        return img;
    }

    private boolean drawTriangle(Graphics2D graphic, double[][] zBuffer, Face face, BufferedImage texture){
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
