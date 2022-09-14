package TileMap;

import Main.Game;

import javax.swing.*;
import java.awt.*;
// Creates and draws background, with options for parallax scrolling
public class Background {
    // Parallax effect
    private int layers;
    private double[] scrollSpeeds = {0.9, 0.8, 0.6, 0.4, 0.2, 0};                                                       // how little the camerax value will effect the x value
    private double[] x;                                                                                                 // stores x values of each layer
    private double cx;

    private String resourceDir;
    private Image[] images;

    public Background(String dir, int layers, double scrollMult, int scroll1, double cx) {
        this.layers = layers;
        resourceDir = dir;
        this.cx = cx;
        init();
    }

    public void init() {
        x = new double[layers];
        for(int i=0; i<layers; i++) {
            x[i] = 0;
        }
        loadImages();
    }
    // Loads resources
    public void loadImages() {
        images = new Image[layers];
        for(int i=0; i<layers; i++) {
            images[i] = new ImageIcon(resourceDir+(i+1)+".png").getImage();
            images[i] = images[i].getScaledInstance(640, 400, Image.SCALE_DEFAULT);
        }
    }

    public void update(double cx) {                                                                                     // adjusts x value of each layer based on given camerax value
        this.cx = cx;                                                                                                   // the further a layer is, the less it is moved when camerax changes
        for(int i=0; i<layers; i++) {
            x[i] = cx*scrollSpeeds[i];
        }
    }

    public void draw(Graphics2D g) {
        for(int i=0; i<layers; i++) {
            g.drawImage(images[i], (int)x[i], 0, null);
            g.drawImage(images[i], (int)x[i] + 1280, 0, -640, 400, null);
            g.drawImage(images[i], (int)x[i] + 1280, 0, null);
            g.drawImage(images[i], (int)x[i] + 2560, 0, -640, 400, null);
        }
    }

}
