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
    private ArrayList<Vector3> normals;
    private ArrayList<Vector3> textures;
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
        normals = new ArrayList<>();
        textures = new ArrayList<>();
        try{
            FileReader reader = new FileReader(objectModel);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine()) != null){
                line = line.replace("  ", " ");
                if(line.startsWith("v ")){
                    String[] parts = line.split(" ");

                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    Vector3 point = new Vector3(x,y,z);
                    vertices.add(point);
                }
                if(line.startsWith("vn ")){
                    String[] parts = (line.split(" "));
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    Vector3 norm = new Vector3(x,y,z);
                    normals.add(norm);
                }
                if(line.startsWith("vt ")){
                    String[] parts = (line.split(" "));
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);

                    Vector3 texture = new Vector3(x,1-y,0);
                    textures.add(texture);
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

                    int[] texture = Arrays.stream(line.split(" ")).skip(1)
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
                    newFace.addNorm(polygon.getFirstVector(), normals.get(norms[0] - 1));
                    newFace.addNorm(polygon.getSecondVector(), normals.get(norms[1] - 1));
                    newFace.addNorm(polygon.getThirdVector(), normals.get(norms[2] - 1));

                    newFace.addTexture(polygon.getFirstVector(), textures.get(texture[0] - 1));
                    newFace.addTexture(polygon.getSecondVector(), textures.get(texture[1] - 1));
                    newFace.addTexture(polygon.getThirdVector(), textures.get(texture[2] - 1));

                    if(Double.compare(textures.get(texture[0] - 1).getX(), 0.680486) == 0 & Double.compare(textures.get(texture[0] - 1).getY(), 0.197526) == 0)
                    {
                        System.out.println(123);
                    }

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
