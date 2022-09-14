package TileMap;

import java.awt.*;
import java.awt.image.BufferedImage;

// Contains image of tile and position ID
public class Tile {

    private Image tileImage;
    private int pos;

    public Tile(Image image, int p) {

        tileImage = image;
        pos = p;

    }

    public Image getTileImage() {
        return tileImage;
    }

    public int getPos() {
        return pos;
    }

}
