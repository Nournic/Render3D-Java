package ru.ssau.tk.nour.image;

import ru.ssau.tk.nour.exceptions.NotObjectFileException;
import ru.ssau.tk.nour.image.data.Point3D;
import ru.ssau.tk.nour.image.data.Polygon;
import ru.ssau.tk.nour.image.other.ImageScale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelObjectReader {
    private final File objectModel;
    private ArrayList<Point3D> vertices;
    private ArrayList<Polygon> polygons;

    public ModelObjectReader(File obj) {
        if(obj == null)
            throw new NullPointerException("File is null");

        String[] fileName = obj.getName().split("\\.");
        if(!fileName[1].equals("obj"))
            throw new NotObjectFileException("File on path " + obj.getAbsolutePath() + " isn't obj file.");

        objectModel = obj;
    }

    public ArrayList<Point3D> getVertices(){
        if(vertices == null)
            readObjFile();

        return vertices;
    }

    private void readObjFile(){
        readVertices();
        readPolygons();
    }

    private void readVertices(){
        vertices = new ArrayList<>();
        try{
            FileReader reader = new FileReader(objectModel);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("v ")){
                    String[] parts = line.split(" ");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    Point3D point = new Point3D(x,y,z);
                    vertices.add(point);
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Polygon> getPolygons() {
        if(polygons == null)
            readObjFile();

        return polygons;
    }

    private void readPolygons(){
        polygons = new ArrayList<>();
        try {
            FileReader reader = new FileReader(objectModel);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine())!=null){
                if(line.startsWith("f ")){
                    int[] points = Arrays.stream(line.split(" ")).skip(1)
                            .map(e->e.split("/")[0])
                            .mapToInt(Integer::parseInt).toArray();

                    Polygon polygon = new Polygon(vertices.get(points[0]-1),
                            vertices.get(points[1]-1),
                            vertices.get(points[2]-1));

                    polygons.add(polygon);
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
