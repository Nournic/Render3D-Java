package ru.ssau.tk.nour.image;

import ru.ssau.tk.nour.exceptions.NotObjectFileException;
import ru.ssau.tk.nour.image.data.Face;
import ru.ssau.tk.nour.image.data.Model;
import ru.ssau.tk.nour.image.data.ModelBuilder;
import ru.ssau.tk.nour.image.data.Polygon;
import ru.ssau.tk.nour.image.other.Vector3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModelObjectReader {
    private final File objectModel;
    private ArrayList<Vector3> vertices;
    private ArrayList<Vector3> normals;
    private ArrayList<Vector3> textures;
    private ArrayList<Face> faces;
    private HashMap<Vector3, ArrayList<Face>> adjacentVertices;

    private Model model;

    public ModelObjectReader(File obj) {
        if(obj == null)
            throw new NullPointerException("File is null");

        String[] fileName = obj.getName().split("\\.");
        if(!fileName[1].equals("obj"))
            throw new NotObjectFileException("File on path " + obj.getAbsolutePath() + " isn't obj file.");

        objectModel = obj;
    }

    public Model getModel(){
        if(model != null)
            return model;

        readObjFile();
        model = new Model(faces, new Vector3(0,0,0));

        return model;
    }

    public ModelBuilder getModelBuilder(){
        if(model != null)
            return new ModelBuilder(faces);

        readObjFile();

        return new ModelBuilder(faces);
    }

    private void calculateNormals(Face face){
        Polygon plg = face.getPlg();
        Vector3 v1 = plg.getFirstVector();
        Vector3 v2 = plg.getSecondVector();
        Vector3 v3 = plg.getThirdVector();

        Vector3 ab = v1.sub(v2);
        Vector3 ac = v2.sub(v3);

        Vector3 norm = ab.cross(ac);

        face.addNorm(v1, norm);
        face.addNorm(v2, norm);
        face.addNorm(v3, norm);
    }

    private void averageNormals(){
        for (Map.Entry<Vector3, ArrayList<Face>> pair: adjacentVertices.entrySet()) {
            Vector3 point = pair.getKey();
            ArrayList<Face> list = pair.getValue();
            Vector3 averageNorm = new Vector3(0,0,0);

            for(Face face: list)
                averageNorm = averageNorm.add(face.getNorm(point));

            averageNorm = averageNorm.mult(1.0/list.size());

            for(Face face: list){
                int index = faces.indexOf(face);
                face.replaceNorm(point, point, averageNorm);
                faces.set(index, face);
            }
        }
    }

    private void readObjFile(){
        readFile();
        readPolygons();
    }

    private void readFile(){
        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        textures = new ArrayList<>();

        try{
            FileReader reader = new FileReader(objectModel);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine()) != null){
                line = line.replace("  ", " ").trim();
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
        adjacentVertices = new HashMap<>();
        boolean emptyNormals = normals.isEmpty();

        try {
            FileReader reader = new FileReader(objectModel);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine())!=null){
                line = line.trim();
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

                    int numPlgs = points.length - 2;
                    for(int i = 0; i < numPlgs; i++){
                        Polygon polygon = new Polygon(
                                vertices.get(points[0] - 1),
                                vertices.get(points[i + 1] - 1),
                                vertices.get(points[i + 2] - 1));

                        Face newFace = new Face(polygon);

                        if (!emptyNormals) {
                            newFace.addNorm(polygon.getFirstVector(), normals.get(norms[0] - 1));
                            newFace.addNorm(polygon.getSecondVector(), normals.get(norms[i + 1] - 1));
                            newFace.addNorm(polygon.getThirdVector(), normals.get(norms[i + 2] - 1));
                        } else
                            calculateNormals(newFace);

                        if (adjacentVertices.containsKey(polygon.getFirstVector()))
                            adjacentVertices.get(polygon.getFirstVector()).add(newFace);
                        else {
                            ArrayList<Face> f = new ArrayList<>();
                            adjacentVertices.put(polygon.getFirstVector(), f);
                        }

                        if (adjacentVertices.containsKey(polygon.getSecondVector()))
                            adjacentVertices.get(polygon.getSecondVector()).add(newFace);
                        else {
                            ArrayList<Face> f = new ArrayList<>();
                            adjacentVertices.put(polygon.getSecondVector(), f);
                        }

                        if (adjacentVertices.containsKey(polygon.getThirdVector()))
                            adjacentVertices.get(polygon.getThirdVector()).add(newFace);
                        else {
                            ArrayList<Face> f = new ArrayList<>();
                            adjacentVertices.put(polygon.getThirdVector(), f);
                        }

                        newFace.addTexture(polygon.getFirstVector(), textures.get(texture[0] - 1));
                        newFace.addTexture(polygon.getSecondVector(), textures.get(texture[i + 1] - 1));
                        newFace.addTexture(polygon.getThirdVector(), textures.get(texture[i + 2] - 1));

                        faces.add(newFace);
                    }
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(emptyNormals)
            averageNormals();
    }
}
