package Objects;

import GameState.StartState;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;


// Class for dialogue system
public class Dialogue {
    // drawing position
    private int x, y = 10, w = 368, h = 30;

    private String resourceDir;
    private int numLines;
    private String[][] contents;
    private int currentLine;
    private int type;
    private int timer;
    private int speaker;
    private Image textbox;
    private Font font1;

    // type constants
    public static final int CALLWAIT = -3;
    public static final int DISPLAYWAIT = -2;
    public static final int QUEUED = -1;

    // speaker constants
    public static final int PLAYER = 1;
    public static final int NARRATOR = 2;

    private boolean addToQueue = false;

    public Dialogue(String dir) {
        InputStream is1 = StartState.class.getResourceAsStream("../Resources/fonts/Novitiate.ttf");
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, is1).deriveFont(20f);
        }
        catch(IOException | FontFormatException e) {
            e.printStackTrace();
        }
        resourceDir = dir;
        loadBox();
        try {
            loadDialogue(resourceDir);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        currentLine = -1;
        nextLine();
    }

    public void loadBox() {
        textbox = new ImageIcon("Resources/gui/gui2.png").getImage();
        textbox = textbox.getScaledInstance(w,h, Image.SCALE_DEFAULT);
    }

    /*
    data is read from data file
    1st line indicates how many dialogue lines
    for each line after:
    values seperated by '|'
    first value indicates type
        -1 - dialogue is queued and will be displayed once all previous have been displayed and will display for time indicated by timer value
        -2 - dialogue is queued but will not stop being displayed until specific event happens and moveToNext() method is called
        -3 - dialogue is queued but wont be displayed until specific event happens and called() method is called
    second last value indicates how many frames the line will be displayed for
    last line indicated who is saying the line
    lines between are Strings to be displayed onto screen
     */
    public void loadDialogue(String dir) throws FileNotFoundException {

        Scanner inFile = new Scanner(new BufferedReader(new FileReader(dir)));
        int numLines = Integer.parseInt(inFile.nextLine());
        contents = new String[numLines][];
        for(int i=0; i<numLines; i++) {
            contents[i] = inFile.nextLine().split("\\|");
        }

    }

    public void update(double cx) {
        String[] current = contents[currentLine];
        type = Integer.parseInt(current[0]);
        speaker = Integer.parseInt(current[current.length-1]);
        if(timer>0) {
            timer--;
            if(type==DISPLAYWAIT) {
                timer = Math.max(1,timer);
            }
        }
        else if(timer == 0 && currentLine<contents.length-1 && type!=CALLWAIT) {
            nextLine();
        }
        x = (int)cx;
    }
    // nextLine moves current line to next line in contents, sets timer depending on dialogue type
    public void nextLine() {
        currentLine++;
        String[] current = contents[currentLine];
        int key = Integer.parseInt(current[0]);
        if(key != CALLWAIT) {
            timer = Integer.parseInt(current[current.length-2]);
        }
    }
    // sets current line to have a type of -1, so timer can go to zero and next lines can be displayed
    public void moveToNext(int key) {
        String[] current = contents[currentLine];
        if(currentLine == key) {
            current[0] = "-1";
        }
    }
    // sets current line to have type -1, and if the key is the same as the current, start the timer
    public void called(int key) {
        String[] current = contents[currentLine];
        if(currentLine == key && type!=QUEUED) {
            timer = Integer.parseInt(current[current.length-2]);
            contents[key][0] = "-1";
        }
        else {
            contents[key][0] = "-1";
        }
    }
    // returns true if all dialogue has been shown
    public boolean getComplete() {
        return timer == 0 && currentLine == contents.length-1 && type==QUEUED;
    }
    public int getCurrent() { return currentLine; }

    public void draw(Graphics2D g) {
        g.setFont(font1);
        g.setColor(Color.DARK_GRAY);
        if(timer > 0) {
            if(speaker == PLAYER) {
                g.drawImage(textbox, (int) x + 20, 20, null);
                for (int i = 1; i < contents[currentLine].length - 2; i++) {
                    g.drawString(contents[currentLine][i], x + 30, 19 + i * 13);
                }
            }
            else {                                                                                                      // position and orientation if speaker is not player
                g.drawImage(textbox, (int) x + 220, 20 + h, w, -h, null);
                for (int i = 1; i < contents[currentLine].length - 2; i++) {
                    g.drawString(contents[currentLine][i], x + 230, 19 + i * 13);
                }
            }
        }
    }

}
