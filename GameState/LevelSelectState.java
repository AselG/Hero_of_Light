package GameState;

import TileMap.Background;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


// Allows player to enter any level, and also access the Tutorial
public class LevelSelectState extends GameState{

    private Background background;

    // Buttons
    private String[] options = {"Tutorial","Level 1","Level 2","Level 3", "Level 4"};
    // corresponding game states
    private int[] optionsStates = {StateMachine.TUTORIAL, StateMachine.LEVEL1STATE, StateMachine.LEVEL2STATE, StateMachine.LEVEL3STATE, StateMachine.LEVEL4STATE};
    private int current = 0;
    private int[] xVals = {260,265,265,265,265};

    private Image[] uiImages;

    private Font titleFont = null;
    private Font font1 = null;


    public LevelSelectState(StateMachine  stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void init() {
        current = 0;
        background = new Background("Resources/backgrounds/background", 6, 2,1, 0);         // all resources loaded
        InputStream is1 = Main.Game.class.getResourceAsStream("../Resources/fonts/DungeonFont.ttf");
        InputStream is2 = StartState.class.getResourceAsStream("../Resources/fonts/DungeonFont.ttf");
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, is1).deriveFont(30f);
            titleFont = Font.createFont(Font.TRUETYPE_FONT, is2).deriveFont(60f);
        }
        catch(IOException | FontFormatException e) {
            e.printStackTrace();
        }
        uiImages = new Image[3];
        for(int i=0; i< uiImages.length; i++) {
            uiImages[i] = new ImageIcon("Resources/gui/gui"+i+".png").getImage();
        }
        uiImages[0] = uiImages[0].getScaledInstance(175,40, Image.SCALE_DEFAULT);
        uiImages[1] = uiImages[1].getScaledInstance(275,370, Image.SCALE_DEFAULT);
    }
    public void update() {}
    public void draw(Graphics2D g) {
        AffineTransform transform = new AffineTransform();
        transform.setToScale(3,3);

        g.setTransform(transform);

        background.draw(g);

        g.drawImage(uiImages[1], 175,20, null);

        g.setColor(Color.WHITE);
        g.setFont(titleFont);
        g.drawString("Levels", 240,100);
        g.setFont(font1);
        for(int i=0; i< options.length; i++) {
            g.drawImage(uiImages[0],220,115+i*50, null);
            if(i == current) {
                g.setColor(Color.RED);
            }
            else {
                g.setColor(Color.WHITE);
            }
            g.drawString(options[i], xVals[i],150+i*50);
        }
    }

    public void setState() {
        stateMachine.setState(optionsStates[current]);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
            current++;
            if(current >= options.length) {
                current = 0;
            }
        }
        if(k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
            current--;
            if(current<0) {
                current = options.length-1;
            }
        }
        if(k == KeyEvent.VK_ENTER) {
            GameState.sfx[GameState.BUTTON].setFramePosition(0);
            GameState.sfx[GameState.BUTTON].start();
            setState();
        }
        if(k == KeyEvent.VK_ESCAPE) {                                                                                   // returns to menu state if escape is pressed
            stateMachine.setState(StateMachine.STARTSTATE);
        }
    }
    public void keyReleased(int k) {}
}
