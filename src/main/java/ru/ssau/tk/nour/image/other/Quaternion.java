package ru.ssau.tk.nour.image.other;

import lombok.Data;
import lombok.Value;

@Value
public class Quaternion {
    double real;
    double i;
    double j;
    double k;

    public Quaternion add(Quaternion other){
        return new Quaternion(
                real + other.real,
                i + other.i,
                j + other.j,
                k + other.k
        );
    }

    public Quaternion sub(Quaternion other){
        return new Quaternion(
                real - other.real,
                i - other.i,
                j - other.j,
                k - other.k
        );
    }

    public Quaternion mult(Quaternion other){
        return new Quaternion(
                real * other.real - i*other.i - j*other.j - k*other.k,
                real * other.i + i*other.real + j*other.k - k*other.j,
                real * other.j - i*other.k + j*other.real + k*other.i,
                real * other.k + i*other.j - j*other.i + k*other.real
        );
    }

    public Quaternion mult(Vector3 other){
        return new Quaternion(
                -this.i * other.getX() - this.j * other.getY() - this.k * other.getZ(),
                this.real * other.getX() + this.j * other.getZ() - this.k * other.getY(),
                this.real * other.getY() - this.i * other.getZ() + this.k * other.getX(),
                this.real * other.getZ() + this.i * other.getY() - this.j * other.getX()
        );
    }

    public Quaternion conj(){
        return new Quaternion(
                real, -i, -j, -k
        );
    }

    public double length(){
        return Math.sqrt(Math.pow(real,2) + Math.pow(i,2) + Math.pow(j,2) + Math.pow(k,2));
    }
}
