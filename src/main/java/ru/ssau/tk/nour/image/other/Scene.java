package ru.ssau.tk.nour.image.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.ssau.tk.nour.image.data.Model;

import java.awt.*;
import java.util.ArrayList;

@Getter
@RequiredArgsConstructor
public class Scene {
    private final ArrayList<Model> models;
    @Setter
    private Color backgroundColor;
}
