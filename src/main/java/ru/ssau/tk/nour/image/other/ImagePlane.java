package ru.ssau.tk.nour.image.other;

import lombok.Value;

@Value
public class ImagePlane {
    private int width;
    private int height;
    private Vector3 topLeftBorder, topRightBorder, bottomLeftBorder, bottomRightBorder;
}
