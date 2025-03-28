package ru.ssau.tk.nour.image.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.ssau.tk.nour.image.other.Matrix3;
import ru.ssau.tk.nour.image.other.Vector3;

import java.util.ArrayList;

@AllArgsConstructor
public class Model {
    private ArrayList<Face> faces;
    @Getter
    @Setter
    private Vector3 pivot;

    public ArrayList<Face> getGlobalFaces(){
        ArrayList<Face> newFaces = new ArrayList<>();

        for(Face face: faces){
            Polygon plg = face.getPlg();
            face.setPlg(new Polygon(
                    plg.getFirstVector().add(pivot),
                    plg.getSecondVector().add(pivot),
                    plg.getThirdVector().add(pivot)
            ));
            face.updateOwnersNorms();

            newFaces.add(face);
        }

        return newFaces;
    }

    public ArrayList<Face> getLocalFaces(){
        return faces;
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
            Vector3 p1 = plg.getFirstVector();
            Vector3 p2 = plg.getSecondVector();
            Vector3 p3 = plg.getThirdVector();

            Vector3 pn1 = rotate.transform(face.getNorm(p1));
            Vector3 pn2 = rotate.transform(face.getNorm(p2));
            Vector3 pn3 = rotate.transform(face.getNorm(p3));

            face.removeNorm(p1);
            face.removeNorm(p2);
            face.removeNorm(p3);

            p1 = rotate.transform(p1);
            p2 = rotate.transform(p2);
            p3 = rotate.transform(p3);

            face.addNorm(p1, pn1);
            face.addNorm(p2, pn2);
            face.addNorm(p3, pn3);

            face.setPlg(new Polygon(p1, p2, p3));
            face.updateOwnersNorms();

            faces.set(i, face);
            //TODO возможно стоит добавить текстуры
        }
    }

    public void move(Vector3 move){
        pivot = pivot.add(move);
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
            faces.set(i, face);
        }
    }

}
