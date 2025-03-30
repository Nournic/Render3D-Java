package ru.ssau.tk.nour.image.other;

import ru.ssau.tk.nour.image.data.Polygon;

import java.util.ArrayList;

public interface ImageTransform {
    ArrayList<Polygon> transform(ArrayList<Polygon> polygons);
}
