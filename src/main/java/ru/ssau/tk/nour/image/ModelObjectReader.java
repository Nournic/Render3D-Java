package ru.ssau.tk.nour.image;

import ru.ssau.tk.nour.exceptions.NotObjectFileException;
import ru.ssau.tk.nour.image.data.Face;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.data.Polygon;
import ru.ssau.tk.nour.image.other.Vector3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelObjectReader {
    private final File objectModel;
    private ArrayList<Vector3> vertices;
    private ArrayList<Vector3> vert_normals;
    private ArrayList<Polygon> polygons;
    private ArrayList<Face> faces;

    private Model model;

    public ModelObjectReader(File obj) {
        if(obj == null)
            throw new NullPointerException("File is null");

        String[] fileName = obj.getName().split("\\.");
        if(!fileName[1].equals("obj"))
            throw new NotObjectFileException("File on path " + obj.getAbsolutePath() + " isn't obj file.");

        objectModel = obj;
    }

    private ArrayList<Vector3> getVertices(){
        if(vertices == null)
            readObjFile();

        return vertices;
    }

    public Model getModel(){
        if(model != null)
            return model;

        readObjFile();
        model = new Model(faces, new Vector3(0,0,0));

        return model;
    }

    private void readObjFile(){
        readVertices();
        readPolygons();
    }

    private void readVertices(){
        vertices = new ArrayList<>();
        vert_normals = new ArrayList<>();
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

                    Vector3 point = new Vector3(x,y,z);
                    vertices.add(point);
                }
                if(line.startsWith("vn ")){
                    String[] parts = line.split(" ");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    Vector3 point = new Vector3(x,y,z);
                    vert_normals.add(point);
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Polygon> getPolygons() {
        if(polygons == null)
            readObjFile();

        return polygons;
    }

    private void readPolygons(){
        faces = new ArrayList<>();

        try {
            FileReader reader = new FileReader(objectModel);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine())!=null){
                if(line.startsWith("f ")){
                    int[] points = Arrays.stream(line.split(" ")).skip(1)
                            .map(e->e.split("/")[0])
                            .mapToInt(Integer::parseInt).toArray();

                    int[] textures = Arrays.stream(line.split(" ")).skip(1)
                            .map(e->e.split("/")[1])
                            .mapToInt(Integer::parseInt).toArray();

                    int[] norms = Arrays.stream(line.split(" ")).skip(1)
                            .map(e->e.split("/")[2])
                            .mapToInt(Integer::parseInt).toArray();

                    Polygon polygon = new Polygon(
                            vertices.get(points[0]-1),
                            vertices.get(points[1]-1),
                            vertices.get(points[2]-1));

                    Face newFace = new Face(polygon);
                    newFace.addNorm(polygon.getFirstVector(), vert_normals.get(norms[0] - 1));
                    newFace.addNorm(polygon.getSecondVector(), vert_normals.get(norms[1] - 1));
                    newFace.addNorm(polygon.getThirdVector(), vert_normals.get(norms[2] - 1));

                    faces.add(newFace);
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
