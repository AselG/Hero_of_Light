package TileMap;

import Main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

// loads tileMap from data file and draws it to screen
public class TileMap {

    // position
    private double x;
    private double y;
    private double maxx;
    private double maxy;

    private String resourceDir;

    // map
    private int[][][] map;
    private int numLayers;
    private int tileSize;
    private int numRows;
    private int numCols;
    private int width;
    private int height;

    // tileset
    private BufferedImage tileset1 = null;
    private BufferedImage tileset2 = null;
    private int numTiles1Across;
    private int numTiles2Across;
    private Tile[][] tiles1;
    private Tile[][] tiles2;

    // drawing
    private int numRowstoDraw;
    private int numColstoDraw;

    public TileMap(String dir) throws IOException {
        tileSize = 16;
        numRowstoDraw = 400/tileSize;
        numColstoDraw = 640/tileSize;
        loadTiles();
        resourceDir = dir;
        loadMap(resourceDir);
        //loadMap("Resources/map1.txt");
    }

    public void loadTiles() throws IOException {                                                                        // loads all tile images from 2 tilesheets

        tileset1 = ImageIO.read(new File("Resources/tileSets/tileSet1.png"));
        numTiles1Across = 84;
        tileset2 = ImageIO.read(new File("Resources/tileSets/tileSet2.png"));
        numTiles2Across = 32;


        tiles1 = new Tile[numTiles1Across][numTiles1Across];
        tiles2 = new Tile[numTiles2Across][numTiles2Across];

        BufferedImage subimage;
        for(int i=0; i<numTiles1Across; i++){
            for(int j=0; j<numTiles1Across; j++){
                subimage = tileset1.getSubimage(i*tileSize, j*tileSize, tileSize, tileSize);
                tiles1[i][j] = new Tile(subimage, i + (j*numTiles1Across));
            }
        }
        for(int i=0; i<numTiles2Across; i++){
            for(int j=0; j<numTiles2Across; j++){
                subimage = tileset2.getSubimage(i*tileSize, j*tileSize, tileSize, tileSize);
                tiles2[i][j] = new Tile(subimage, i + (j*numTiles2Across));
            }
        }



    }

    public void loadMap(String s) throws FileNotFoundException {                                                        // loads map data from data file

        Scanner inFile = new Scanner(new BufferedReader(new FileReader(s)));

        width = numCols*tileSize;
        height = numRows*tileSize;

        numLayers = Integer.parseInt(inFile.nextLine());                                                                // first line indicates number of layers to image
        numCols = Integer.parseInt(inFile.nextLine());                                                                  // second and third line indicate number of rows and number of columns in map
        numRows = Integer.parseInt(inFile.nextLine());
        maxx = numCols * 16 - Game.WIDTH;
        maxy = numRows * 16 - Game.HEIGHT;

        map = new int[numLayers][numRows][numCols];                                                                     // array of 2d arrays, each 2d array represents one layer in map

        /*
        to create maps I used a software called Tiled, which allows me to load the tilemaps I'm using, and basically
        draw out a map using the tiles in the tilesheet. After drawing the map however I want, if I open up the Tiled file (.tmx file) in notepad,
        the file contains a bunch of lines, where each line is a row in the tile map, and within a line there is a bunch of numbers seperated by commas.
        Each number represents a tile ID, each tile in a loaded tilesheet has an ID which is calculated by row# = floor(ID/#ofRowsInTilesheet) and col# = ID%#RowsInTileSheet
        If I copy and paste all the grids of ID's (each grid represents an individual layer) into a seperate file, I can reconstruct the map image by getting the tile based on its tile ID
        and its placement within the grid of that layer. I can draw all the tiles at their corresponding positions onto a buffered image and then display this buffered image in the draw() method
         */
        for(int layer=0; layer<numLayers; layer++){
            for(int i=0; i<numRows; i++){
                String line = inFile.nextLine();
                String[] nums = line.split(",");
                for(int j=0; j<numCols; j++){
                    map[layer][i][j] = Integer.parseInt(nums[j]);
                }
            }
        }

    }

    public void draw(Graphics2D g) {
        BufferedImage image = new BufferedImage(numCols*tileSize,numRows*tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics buffG = image.getGraphics();
        for(int layer=0; layer<numLayers; layer++){                                                                     // starts with furthest back layer
            for(int i=0; i<numRows; i++){                                                                               // draws tiles to buffered image
                for(int j=0; j<numCols; j++){
                    int rc = map[layer][i][j];
                    if(rc<=7056){
                        if(rc>0){
                            rc-=1;
                        }
                        int row = rc/numTiles1Across;
                        int col = rc%numTiles1Across;


                        buffG.drawImage(tiles1[col][row].getTileImage(), (j*tileSize), (i*tileSize), null);
                    }
                    else{
                        rc-=7056;
                        if(rc>0){
                            rc-=1;
                        }
                        int row = rc/numTiles2Across;
                        int col = rc%numTiles2Across;


                        buffG.drawImage(tiles2[col][row].getTileImage(), (j*tileSize), (i*tileSize), null);
                    }

                }
            }
        }
        g.drawImage(image, 0, 0, null);                                                                     // draws the buffered image
    }
    // Getters and Setters
    public double getX() { return x; }
    public double getWidth() { return numCols*tileSize; }


}
