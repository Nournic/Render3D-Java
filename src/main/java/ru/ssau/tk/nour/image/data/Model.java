package ru.ssau.tk.nour.image.data;

import lombok.Getter;
import lombok.Setter;
import ru.ssau.tk.nour.image.other.Matrix3;
import ru.ssau.tk.nour.image.other.Quaternion;
import ru.ssau.tk.nour.image.other.Vector3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Model {
    private final ArrayList<Face> faces;
    @Getter
    private Vector3 pivot;
    @Getter
    @Setter
    private BufferedImage texture;

    public Model(ArrayList<Face> faces, Vector3 pivot){
        this.pivot = pivot;
        this.faces = faces;
    }

    public ArrayList<Face> getGlobalFaces(){
        ArrayList<Face> newFaces = new ArrayList<>();

        for(Face face: faces){
            Polygon plg = face.getPlg();
            Face newFace = new Face(new Polygon(
                    plg.getFirstVector().add(pivot),
                    plg.getSecondVector().add(pivot),
                    plg.getThirdVector().add(pivot)
            ));

            newFace.addNorm(newFace.getPlg().getFirstVector(), face.getNorm(plg.getFirstVector()));
            newFace.addNorm(newFace.getPlg().getSecondVector(), face.getNorm(plg.getSecondVector()));
            newFace.addNorm(newFace.getPlg().getThirdVector(), face.getNorm(plg.getThirdVector()));

            newFace.addTexture(newFace.getPlg().getFirstVector(), face.getTexture(plg.getFirstVector()));
            newFace.addTexture(newFace.getPlg().getSecondVector(), face.getTexture(plg.getSecondVector()));
            newFace.addTexture(newFace.getPlg().getThirdVector(), face.getTexture(plg.getThirdVector()));

            newFaces.add(newFace);
        }

        return newFaces;
    }

    public ArrayList<Face> getLocalFaces(){
        return faces;
    }

    private Vector3 rotateVector(Quaternion q, Vector3 v){
        Quaternion t = q.mult(v).mult(q.conj());

        return new Vector3(t.getI(), t.getJ(), t.getK());
    }

    public void rotate(Vector3 rotateVector, double rotateAngle){
        rotateVector = rotateVector.mult(1.0/rotateVector.length());
        Quaternion q = new Quaternion(Math.cos(rotateAngle/2.0),
                rotateVector.getX() * Math.sin(rotateAngle/2.0),
                rotateVector.getY() * Math.sin(rotateAngle/2.0),
                rotateVector.getZ() * Math.sin(rotateAngle/2.0));

        for(int i = 0; i < faces.size(); i++){
            Face face = faces.get(i);

            Polygon plg = face.getPlg();

            Vector3 pt1 = face.getTexture(plg.getFirstVector());
            Vector3 pt2 = face.getTexture(plg.getSecondVector());
            Vector3 pt3 = face.getTexture(plg.getThirdVector());

            Vector3 p1 = plg.getFirstVector();
            Vector3 p2 = plg.getSecondVector();
            Vector3 p3 = plg.getThirdVector();

            Vector3 pn1 = rotateVector(q, face.getNorm(p1));
            Vector3 pn2 = rotateVector(q, face.getNorm(p2));
            Vector3 pn3 = rotateVector(q, face.getNorm(p3));

            p1 = rotateVector(q, p1);
            p2 = rotateVector(q, p2);
            p3 = rotateVector(q, p3);

            face.setPlg(new Polygon(p1, p2, p3));
            face.replaceNorm(plg.getFirstVector(), face.getPlg().getFirstVector(),  pn1);
            face.replaceNorm(plg.getSecondVector(), face.getPlg().getSecondVector(), pn2);
            face.replaceNorm(plg.getThirdVector(), face.getPlg().getThirdVector(), pn3);

            face.replaceTexture(plg.getFirstVector(), face.getPlg().getFirstVector(), pt1);
            face.replaceTexture(plg.getSecondVector(), face.getPlg().getSecondVector(), pt2);
            face.replaceTexture(plg.getThirdVector(), face.getPlg().getThirdVector(), pt3);

            faces.set(i, face);
            //TODO возможно стоит добавить текстуры
        }
    }

    public void rotate(double alpha, double beta, double gamma){
        Matrix3 zTransform = new Matrix3(new double[]{
                Math.cos(gamma), Math.sin(gamma), 0,
                -Math.sin(gamma), Math.cos(gamma), 0,
                0, 0, 1
        });

        Matrix3 yTransform = new Matrix3(new double[] {
                Math.cos(beta), 0, -Math.sin(beta),
                0, 1, 0,
                Math.sin(beta), 0, Math.cos(beta)
        });
        Matrix3 xTransform = new Matrix3(new double[] {
                1, 0, 0,
                0, Math.cos(alpha), Math.sin(alpha),
                0, -Math.sin(alpha), Math.cos(alpha)
        });

        Matrix3 rotate = xTransform.multiply(yTransform.multiply(zTransform));

        for(int i = 0; i < faces.size(); i++){
            Face face = faces.get(i);

            Polygon plg = face.getPlg();

            Vector3 pt1 = face.getTexture(plg.getFirstVector());
            Vector3 pt2 = face.getTexture(plg.getSecondVector());
            Vector3 pt3 = face.getTexture(plg.getThirdVector());

            Vector3 p1 = plg.getFirstVector();
            Vector3 p2 = plg.getSecondVector();
            Vector3 p3 = plg.getThirdVector();

            Vector3 pn1 = rotate.transform(face.getNorm(p1));
            Vector3 pn2 = rotate.transform(face.getNorm(p2));
            Vector3 pn3 = rotate.transform(face.getNorm(p3));

            p1 = rotate.transform(p1);
            p2 = rotate.transform(p2);
            p3 = rotate.transform(p3);

            face.setPlg(new Polygon(p1, p2, p3));
            face.replaceNorm(plg.getFirstVector(), face.getPlg().getFirstVector(),  pn1);
            face.replaceNorm(plg.getSecondVector(), face.getPlg().getSecondVector(), pn2);
            face.replaceNorm(plg.getThirdVector(), face.getPlg().getThirdVector(), pn3);

            face.replaceTexture(plg.getFirstVector(), face.getPlg().getFirstVector(), pt1);
            face.replaceTexture(plg.getSecondVector(), face.getPlg().getSecondVector(), pt2);
            face.replaceTexture(plg.getThirdVector(), face.getPlg().getThirdVector(), pt3);

            faces.set(i, face);
            //TODO возможно стоит добавить текстуры
        }
    }

    public void move(Vector3 move){
        pivot = pivot.add(move);
    }

    public void move(double x, double y, double z){
        pivot = pivot.add(new Vector3(x,y,z));
    }

    public void scale(double alpha){
        for(int i = 0; i < faces.size(); i++){
            Face face = faces.get(i);
            Polygon plg = face.getPlg();
            face.setPlg(new Polygon(
                    plg.getFirstVector().mult(alpha),
                    plg.getSecondVector().mult(alpha),
                    plg.getThirdVector().mult(alpha)
            ));

            face.replaceNorm(plg.getFirstVector(), face.getPlg().getFirstVector(),  face.getNorm(plg.getFirstVector()));
            face.replaceNorm(plg.getSecondVector(), face.getPlg().getSecondVector(), face.getNorm(plg.getSecondVector()));
            face.replaceNorm(plg.getThirdVector(), face.getPlg().getThirdVector(), face.getNorm(plg.getThirdVector()));

            face.replaceTexture(plg.getFirstVector(), face.getPlg().getFirstVector(), face.getTexture(plg.getFirstVector()));
            face.replaceTexture(plg.getSecondVector(), face.getPlg().getSecondVector(), face.getTexture(plg.getSecondVector()));
            face.replaceTexture(plg.getThirdVector(), face.getPlg().getThirdVector(), face.getTexture(plg.getThirdVector()));
            faces.set(i, face);
        }
    }

}
