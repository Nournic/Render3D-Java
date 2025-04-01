package ru.ssau.tk.nour.raytracer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ssau.tk.nour.image.other.Vector3;

@AllArgsConstructor
@Getter
public class Camera {
    Vector3 pos;
    Vector3 origin;
}
