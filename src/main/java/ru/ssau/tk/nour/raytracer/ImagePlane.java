package ru.ssau.tk.nour.raytracer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ssau.tk.nour.image.other.Vector3;

@Getter
@AllArgsConstructor
public class ImagePlane {
    Vector3 topLeft, topRight, bottomLeft, bottomRight;
}
