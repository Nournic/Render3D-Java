package ru.ssau.tk.nour.image.data;

import lombok.Getter;
import lombok.Setter;
import ru.ssau.tk.nour.image.other.Vector3;

import java.util.HashMap;


public class Face {
    @Getter
    @Setter
    private Polygon plg;

    private final HashMap<Vector3, Vector3> norms;
    private final HashMap<Vector3, Vector3> textures;

    public Face(Polygon plg) {
        this.plg = plg;
        this.norms = new HashMap<>();
        this.textures = new HashMap<>();
    }

    public void addNorm(Vector3 point, Vector3 norm){
        norms.put(point, norm);
    }

    public void replaceNorm(Vector3 point, Vector3 norm){
        norms.replace(point, norm);
    }
    public void replaceNorm(Vector3 owner, Vector3 newOwner, Vector3 norm){
        Vector3 vn = norms.get(owner);
        removeNorm(owner);
        norms.put(newOwner, vn);
    }


    public void removeNorm(Vector3 point){
        norms.remove(point);
    }


    public void updateOwnersNorms(){

        Vector3[] list = norms.values().toArray(new Vector3[2]);
        norms.clear();

        addNorm(plg.getFirstVector(), list[0]);
        addNorm(plg.getSecondVector(), list[1]);
        addNorm(plg.getThirdVector(), list[2]);
    }

    public Vector3 getNorm(Vector3 point){
        return norms.get(point);
    }

    public void addTexture(Vector3 point, Vector3 texture){
        textures.put(point, texture);
    }

    public void replaceTexture(Vector3 point, Vector3 texture){
        textures.replace(point, texture);
    }

    public Vector3 getTexture(Vector3 point){
        return textures.get(point);
    }
}
