package ru.ssau.tk.nour.image.data;

import ru.ssau.tk.nour.image.other.Quaternion;
import ru.ssau.tk.nour.image.other.Vector3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ModelBuilder {
    private final Model model;

    public ModelBuilder(ArrayList<Face> val){
        model = new Model(val, new Vector3(0,0,0));
    }

    public ModelBuilder rotate(double alpha, double beta, double gamma){
        model.rotate(alpha, beta, gamma);
        return this;
    }

    public ModelBuilder rotate(int alpha, int beta, int gamma){
        model.rotate(Math.toRadians(alpha), Math.toRadians(beta), Math.toRadians(gamma));
        return this;
    }

    public ModelBuilder rotate(Vector3 rotateVector, double rotateAngle){
        model.rotate(rotateVector, rotateAngle);
        return this;
    }

    public ModelBuilder move(Vector3 move){
        model.move(move);
        return this;
    }

    public ModelBuilder move(double x, double y, double z){
        model.move(x,y,z);
        return this;
    }

    public ModelBuilder scale(double alpha){
        model.scale(alpha);
        return this;
    }

    public ModelBuilder setTexture(BufferedImage img){
        model.setTexture(img);
        return this;
    }

    public Model build(){
        return model;
    }
}
