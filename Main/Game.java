
/*
        HERO OF LIGHT
    by Asel Gamage

    This is side scrolling, fantasy, RPG-styled game with a story arc, numerous levels, and intense boss fights

    One of my top priorities when creating this game was making the player's movement unique, satisfying, and
    advanced. To do this, I put a large emphasis on using vertical movement to dodge enemy attacks, and get around the
    maps. I implemented a double jump feature, alongside a somersault which you can do by double jumping while running.
    Somersaults give a burst of horizontal momentum allowing you to dodge and bait enemy attacks effectively after
    sufficient practises.

    Another focus was making a skillful combat system with different ways to play. Using the airAttack sequence allows
    you to get high ground on opponents and come crashing down on the last attack to deal large chunks of damage, while
    attacking from the ground gives you more mobility options (player can lunge after each attack by holding movement
    keys during attack sequence.

    To create and develop a story within a game, I needed a dialogue system which was completely seperated from the data
    so I could use it in multiple levels, and adaptable to the different use cases (for example, leaving text on screen
    until some action happens or waiting for an event to trigger a dialogue box).

    I also wanted the game to look attractive and have very unique maps that played around the players movement abilities
    I spent a lot of time making each custom map and adding small details to make each level interesting and appealing
    to the eye.

    Any fantasy RPG-style game needs some sort of magic, so I added two spells/abilities to my game. Abilities are
    unlocked by playing through levels 2 and 3.

    Overall, I believe my game is fun to play, with an interesting story which is continued through each level.
    Hope you enjoy!

    PS - All features explained more in depth in individual classes
 */

/* CONTROLS
    (controls also explained in Tutorial level,
    from Menu screen, go to Level Select, select Tutorial)

    A - move left
    D - move right
    W/SPACE - jump
         -- double jump
         -- double jump with movement key to do a flip
    K - attack
         -- hold to do combo attack
         -- hold with movement keys to lunge
         -- activate and hold in air to do combo air-attack
    L - cast spell1 (Lightning Blade)
    J - cast spell2 (Divine Punishment)
 */
package Main;

import GameState.StateMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Game extends JFrame {
    GamePanel game;

    // dimensions
    public static final int WIDTH = 640;
    public static final int HEIGHT = 400;

    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 800;

    public Game() throws IOException {
        super("Final Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game = new GamePanel();
        add(game);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        Game frame = new Game();
    }

}

class GamePanel extends JPanel implements KeyListener, ActionListener {

    // Dimensions in game
    public static final int WIDTH = 640;
    public static final int HEIGHT = 400;
    // Dimensions of window being displayed
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 800;

    // Timer
    private Timer myTimer;

    private StateMachine stateMachine;

    private Graphics g;

    public GamePanel() throws IOException {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        addKeyListener(this);
        // Timer
        myTimer = new Timer(30, this);
        setFocusable(true);
        requestFocus();

        init();

    }

    public void init() throws IOException {
        stateMachine = new StateMachine();
        myTimer.start();
    }

    private void update() {
        stateMachine.update();
    }

    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        // passes Graphics2D object onto stateMachine to pass to current game state
        stateMachine.draw(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    public void	keyPressed(KeyEvent e) {
        stateMachine.keyPressed(e.getKeyCode());
    }
    public void	keyReleased(KeyEvent e) {
        stateMachine.keyReleased(e.getKeyCode());
    }
    public void	keyTyped(KeyEvent e) {}

}
