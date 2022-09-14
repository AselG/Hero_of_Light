package GameState;

import Main.Game;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*
    The stateMachine class will manage what state the game is in, and hold an ArrayList of all
    game states. It will also call the initialize, update, and draw methods for each state
 */
public class StateMachine {

    private ArrayList<GameState> gameStates;                                                                            // contains all game states
    private int current;                                                                                                // holds the index of the current state

    // constants for gameStates ArrayList
    public static final int STARTSTATE = 0;
    public static final int PLAYSTATE = 1;
    public static final int TUTORIAL = 2;
    public static final int LEVEL1STATE = 3;
    public static final int LEVEL2STATE = 4;
    public static final int LEVEL3STATE = 5;
    public static final int LEVEL4STATE = 6;
    public static final int LEVELSELECTSTATE = 7;
    public static final int ABOUTSTATE = 8;
    public static final int PAUSESTATE = 9;
    public static final int LEVELFAILEDSTATE = 10;
    public static final int LEVELCOMPLETESTATE = 11;


    private int currentLevel = LEVEL1STATE;                                                                             // holds index of the most recent game level the player was in
                                                                                                                        // for example, needed for when pressing 'Restart Level'

    public StateMachine() throws IOException {

        gameStates = new ArrayList<GameState>();

        current = STARTSTATE;
        gameStates.add(new StartState(this));                                                                // all game states added to arraylist
        gameStates.add(new PlayState(this));
        gameStates.add(new TutorialState(this));
        gameStates.add(new Level1State(this));
        gameStates.add(new Level2State(this));
        gameStates.add(new Level3State(this));
        gameStates.add(new Level4State(this));
        gameStates.add(new LevelSelectState(this));
        gameStates.add(new AboutState(this));
        gameStates.add(new PauseState(this));
        gameStates.add(new LevelFailedState(this));
        gameStates.add(new LevelCompleteState(this));

        gameStates.get(current).init();                                                                                 // initializes first state


    }

    // Used to switch between states, takes in index of next state as parameter
    public void setState(int state) {
        try {
            current = state;
            gameStates.get(current).init();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(current == TUTORIAL || current == LEVEL1STATE || current == LEVEL2STATE || current == LEVEL3STATE || current == LEVEL4STATE) {
            currentLevel = current;                                                                                     // updates currentLevel if the state being switched to is a game level
        }
    }
    public int getCurrentLevel() { return currentLevel; }

    public void update(){
        gameStates.get(current).update();
    }

    public void draw(Graphics2D g) {
        gameStates.get(current).draw(g);
    }

    public void keyPressed(int k) {                                                                                     // passes pressed keys onto the current state
        gameStates.get(current).keyPressed(k);
    }

    public void keyReleased(int k) {
        gameStates.get(current).keyReleased(k);
    }

}
