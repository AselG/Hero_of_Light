package GameState;

import Main.Game;
import Objects.*;
import TileMap.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*
    In Level 1, the player is introduced to this alternate world, where darnkess has overrun earth.
    They then experience their first encounter with minions of Darkness, and defeat a miniboss.
    After completing the combat segment, the player communicates with the Forces of Light above
    and learns about his predicament
 */
public class Level1State extends GameState{

    private double cameraX = 0;                                                                                         // holds x position of camera

    // Background and level map
    private TileMap tileMap;
    private Background background;

    // Objects
    public Map map;
    public Player player;
    public Enemy2 enemy;
    public Enemy1 boss;

    // Dialogue
    public Dialogue dialogue;

    // Attacks
    public ArrayList<Attack> playerAttacks = new ArrayList<Attack>();
    public ArrayList<Attack> enemyAttacks = new ArrayList<Attack>();
    // no spells because spells are not unlocked until level2

    // everything is loaded in initialize method as opposed to constructur because data should be reset when player exits
    // and re-enters level 1. Initialize method is called whenever stateMachine switched the current state, but constructor
    // is only called when class is created on start up
    public Level1State(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void init() {
        // all resources loaded when initialized
        // enemy images loaded in static methods on initialization to improve efficiency
        Enemy1.loadImages("Resources/Enemy/Enemy03/");
        Enemy2.loadImages("Resources/Enemy/Enemy05/");
        try {
            tileMap = new TileMap("Resources/Level1/map.txt");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        background = new Background("Resources/backgrounds/background", 6, 2,1, cameraX);

        map = new Map("Resources/Level1/mapPoly.txt");

        player = new Player(map);
        boss = new Enemy1("Resources/Enemy/Enemy03/", 2180,240, 30, 35, 1, 100);
        enemy = new Enemy2("Resources/Enemy/Enemy05/", 1000,100, 15,35,3,20);

        dialogue = new Dialogue("Resources/Level1/Dialogue.txt");
    }

    public void update() {
        if(player.getHP() <= 0) {
            stateMachine.setState(StateMachine.LEVELFAILEDSTATE);                                                       // state switched if player dies
        }
        // Dialogue events
        if(player.getX()>=750) {                                                                                        // events were dialogue would be waiting to continue,
            dialogue.called(4);                                                                                     // if event happens, dialogue is told that it can continue
        }                                                                                                               // (explained more in depth in dialogue class)
        if(enemy.getHP()<=0) {
            dialogue.called(9);
        }
        if(player.getX()>=1820) {
            dialogue.called(10);
        }
        if(boss.getState() == Enemy1.DEAD) {
            dialogue.called(12);
        }

        playerAttacks.clear();                                                                                          // clear attacks for player and enemy
        enemyAttacks.clear();

        cameraX = player.getX() - Game.WIDTH/2;                                                                         // camera follows player and keeps player unless player is toward
        checkCameraBounds();                                                                                            // an edge

        background.update(cameraX);

        player.update();                                                                                                // player updated

        if(enemy.getHP() >= 0) {                                                                                        // updates enemy if it is alive
            enemy.update(map, player);
            if(enemy.getAttackMove() != null) {
                enemyAttacks.add(enemy.getAttackMove());
            }
        }

        if(boss.getState() == Enemy1.ALIVE) {                                                                           // updates boss if it is alive
            boss.update(map, player);
            if (boss.getAttackMove() != null) {
                enemyAttacks.add(boss.getAttackMove());
            }
        }

        if(player.getAttackMove() != null) {                                                                            // if player is currently attacking, add attack to playerAttack arrayList
            playerAttacks.add(player.getAttackMove());
        }

        for(Attack attack:playerAttacks) {                                                                              // check playerAttacks for collision with all enemies
            if(attack.collide(enemy)) {
                enemy.setHP(attack.getDmg());
            }
            if(attack.collide(boss)) {
                boss.setHP(attack.getDmg());
            }
        }
        for(Attack attack:enemyAttacks) {                                                                               // check all enemy attacks for collision with player
            if(attack.collide(player)) {
                player.setHP(attack.getDmg());
            }
        }

        dialogue.update(cameraX);                                                                                       // dialogue updated

        if(dialogue.getComplete()) {                                                                                    // dialogue after boss dies, so level is complete once dialogue is done
            stateMachine.setState(StateMachine.LEVELCOMPLETESTATE);
            System.out.println("LEVEL COMPLETE");
        }
    }

    public void checkCameraBounds() {                                                                                   // prevents camera from going off the map when player goes close to edge
        if(cameraX < 0) {
            cameraX = 0;
        }
        if(cameraX > tileMap.getWidth() - Game.WIDTH) {
            cameraX = tileMap.getWidth() - Game.WIDTH;
        }
    }

    public void draw(Graphics2D g) {
        AffineTransform transform = new AffineTransform();
        transform.setToScale(3,3);                                                                               // scales up game to fit JPanel
        transform.translate(-cameraX,0);                                                                             // translates the display based on camera position

        g.setTransform(transform);

        background.draw(g);
        tileMap.draw(g);

        enemy.draw(g);
        boss.draw(g);
        player.draw(g);
        player.drawHUD(g, (int)cameraX);
        for(Attack attack:playerAttacks) {
            attack.draw(g);
        }
        for(Attack attack:enemyAttacks) {
            attack.draw(g);
        }
        dialogue.draw(g);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ESCAPE) {                                                                                   // pause menu shown if escape is pressed
            stateMachine.setState(StateMachine.PAUSESTATE);
        }
        player.keyPressed(k);
    }
    public void keyReleased(int k) {
        player.keyReleased(k);
    }
}
