package ru.ssau.tk.nour.image.other;

public class Vector3 {
    private final double x, y, z;
    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 other){
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 sub(Vector3 other){
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3 mult(double alpha){
        return new Vector3(alpha * this.x, alpha * this.y, alpha * this.z);
    }

    public double scalar(Vector3 other){
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double length(){
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x);
    }

    public Vector3 rotate(double angle, Axis axis){
        switch(axis){
            case Axis.XAxis -> {
                return new Vector3(
                        x,
                        y * Math.cos(angle) + z * Math.sin(angle),
                        y * -Math.sin(angle) + z * Math.cos(angle)
                );
            }
            case Axis.YAxis -> {
                return new Vector3(
                        x * Math.cos(angle) + z * Math.sin(angle),
                        y,
                        x * -Math.sin(angle) + z * Math.cos(angle)
                );
            }
        }
        return new Vector3(
                x * Math.cos(angle) + y * Math.sin(angle),
                x * -Math.sin(angle) + y * Math.cos(angle),
                z
        );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}


