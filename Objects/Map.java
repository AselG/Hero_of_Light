package Objects;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

// Map class contains all physical objects in a level as polygons, used to check for collision during movement
public class Map {

    // Collision
    private Polygon[] polygons;                                                                                         // platforms and ground stored as polygons
    private String resourceDir;                                                                                         // all data is read from a data file

    public Map(String dir){
        resourceDir = dir;
        try{
            loadPolygon(resourceDir);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadPolygon(String s) throws FileNotFoundException {

        Scanner inFile = new Scanner(new BufferedReader(new FileReader(s)));

        int numPolys = Integer.parseInt(inFile.nextLine());                                                             // first line indicates number of polygons
        polygons = new Polygon[numPolys];
        for(int i=0; i<numPolys; i++) {
            polygons[i] = new Polygon();
            int numPoints = Integer.parseInt(inFile.nextLine());                                                        // first line for each polygon indicates number of vertices
            for(int j=0; j<numPoints; j++){                                                                             // each line after contains a vertice
                String line = inFile.nextLine();                                                                        // adds each vertice to corresponding polygon object
                String[] pt = line.split(",");
                polygons[i].addPoint(Integer.parseInt(pt[0]),Integer.parseInt(pt[1]));
            }
        }

    }
    public Polygon[] getPolygon() { return polygons; }
}
