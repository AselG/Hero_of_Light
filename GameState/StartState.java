package GameState;

import TileMap.Background;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

/*
    Start state is the menu screen of the game, shown on startup or whenever user goes back to menu
    From start state, user can enter into level 1, go to level select screen, or exit the game
 */
public class StartState extends GameState{
    // Background
    private Background background;

    // On screen buttons
    private String[] options = {"Enter Game","Level Select"};                                            // Strings for each button
    private int[] optionsStates = {StateMachine.LEVEL1STATE, StateMachine.LEVELSELECTSTATE};// maps each button to an actual game state
    private int current = 0;                                                                                            // currently selected button
    private int[] xVals = {247,238,270,280};                                                                            // x values needed to have the text centered

    private Image[] uiImages;                                                                                           // various images for buttons, panels, etc.

    // Fonts
    private Font titleFont = null;
    private Font font1 = null;

    public StartState(StateMachine  stateMachine) {
        this.stateMachine = stateMachine;
        background = new Background("Resources/backgrounds/background", 6, 2,1, 0);         // all resources loaded
        InputStream is1 = StartState.class.getResourceAsStream("../Resources/fonts/DungeonFont.ttf");
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

    public void init() {

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
        g.drawString("Hero of", 220,100);
        g.drawString("Light", 245,160);
        g.setFont(font1);
        for(int i=0; i< options.length; i++) {
            g.drawImage(uiImages[0],220,195+i*50, null);
            if(i == current) {
                g.setColor(Color.RED);
            }
            else {
                g.setColor(Color.WHITE);
            }
            g.drawString(options[i], xVals[i],230+i*50);                                                             // corresponding x value used to center text
        }
    }

    public void setState() {
        stateMachine.setState(optionsStates[current]);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {                                                               // keyboard input used to navigate UI
            current++;
            if(current >= options.length) {
                current = 0;
            }
        }
        if(k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {                                                                 // arrow keys or WASD can be used
            current--;
            if(current<0) {
                current = options.length-1;
            }
        }
        if(k == KeyEvent.VK_ENTER) {
            GameState.sfx[GameState.BUTTON].setFramePosition(0);                                                        // button sound effect played
            GameState.sfx[GameState.BUTTON].start();
            setState();
        }
    }
    public void keyReleased(int k) {}
}
