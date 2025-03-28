package ru.ssau.tk.nour.image.data;

import lombok.EqualsAndHashCode;
import ru.ssau.tk.nour.image.other.Vector3;

@EqualsAndHashCode
public class Polygon {
    private final Vector3 v1;
    private final Vector3 v2;
    private final Vector3 v3;

    public Polygon(Vector3 v1, Vector3 v2, Vector3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Vector3 getFirstVector() {
        return v1;
    }

    public Vector3 getSecondVector() {
        return v2;
    }

    public Vector3 getThirdVector() {
        return v3;
    }
}
