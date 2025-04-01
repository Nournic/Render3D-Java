package ru.ssau.tk.nour;

import ru.ssau.tk.nour.image.other.Vector3;
import ru.ssau.tk.nour.raytracer.Camera;
import ru.ssau.tk.nour.raytracer.ImagePlane;

public class RayTracer {
    private static final int WIDTH = 256;
    private static final int HEIGHT = 192;

    public static void main(String[] args) {
        ImagePlane plane = new ImagePlane(
                new Vector3(1, 0.75, 0),
                new Vector3(-1, 0.75, 0),
                new Vector3(1, -0.75, 0),
                new Vector3(-1, -0.75, 0)
        );

        Camera camera = new Camera(new Vector3(0,0,-1));

    }
}
