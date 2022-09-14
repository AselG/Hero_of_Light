package GameState;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

// Abstract class for all gameStates
// all game states inherit init(), update(), draw(), keyPressed(), and keyReleased() methods
public abstract class GameState {

    public static File[] sfxFiles = {new File("Resources/sfx/button.wav"),
            new File("Resources/sfx/sword.wav"),
            new File("Resources/sfx/lightning_blade.wav"),
            new File("Resources/sfx/lightning_strike.wav")};

    public static Clip[] sfx = new Clip[4];                                                                             // public variable for sound effects, accessed in Player class,
    static {                                                                                                            // and all non-level states
        for(int i=0; i<sfx.length; i++) {
            try {
                sfx[i] = AudioSystem.getClip();
                sfx[i].open(AudioSystem.getAudioInputStream(sfxFiles[i]));                                              // sfx loaded
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    // Constants for sfx array
    public static final int BUTTON = 0;
    public static final int SWORD = 1;
    public static final int LIGHTNING1 = 2;
    public static final int LIGHTNING2 = 3;

    protected StateMachine stateMachine;

    public abstract void init() throws FileNotFoundException;
    public abstract void update();
    public abstract void draw(Graphics2D g);
    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);


}
