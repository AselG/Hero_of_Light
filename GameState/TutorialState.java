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

public class TutorialState extends GameState{

    private double cameraX = 0;
    private double cameraY = 0;

    private TileMap tileMap;
    private Background background;

    // Objects
    public Map map;
    public Player player;
    public Enemy2 enemy;
    public Enemy1 boss;

    public Dialogue dialogue;
    private ArrayList<Integer> timings = new ArrayList<Integer>();

    // Attacks
    public ArrayList<Attack> playerAttacks = new ArrayList<Attack>();
    public ArrayList<Attack> enemyAttacks = new ArrayList<Attack>();

    public TutorialState(StateMachine  stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void init() {

        Enemy1.loadImages("Resources/Enemy/Enemy03/");
        Enemy2.loadImages("Resources/Enemy/Enemy05/");
        try {
            tileMap = new TileMap("Resources/Level1/map.txt");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        background = new Background("Resources/backgrounds/background", 6, 2,1, cameraX);

        map = new Map("Resources/Level1/mapPoly.txt");

        player = new Player(map);
        //spells.add(new Spell(player.getX(), player.getY(), 0));
        boss = new Enemy1("Resources/Enemy/Enemy03/", 2180,240, 30, 35, 1, 100);
        enemy = new Enemy2("Resources/Enemy/Enemy05/", 1000,100, 15,35,3,20);

        dialogue = new Dialogue("Resources/Tutorial/Dialogue.txt");

    }

    public void update() {
        if(player.getX() >= 760) {
            dialogue.called(8);
        }
        if(enemy.getHP()<=0) {
            dialogue.called(10);
        }
        if(player.getX() >= 1700) {
            dialogue.called(11);
        }
        if(boss.getState() == Enemy1.DEAD) {
            dialogue.called(12);
        }
        if(boss.getState() == Enemy1.DEAD) {
            stateMachine.setState(StateMachine.LEVELCOMPLETESTATE);
        }

        playerAttacks.clear();
        enemyAttacks.clear();

        cameraX = player.getX() - Game.WIDTH/2;
        checkCameraBounds();

        background.update(cameraX);

        player.update();

        enemy.update(map, player);
        if(enemy.getAttackMove() != null) {
            enemyAttacks.add(enemy.getAttackMove());
        }

        boss.update(map, player);
        if(boss.getAttackMove() != null) {
            enemyAttacks.add(boss.getAttackMove());
        }

        if(player.getAttackMove() != null) {
            playerAttacks.add(player.getAttackMove());
        }

        for(Attack attack:playerAttacks) {
            if(attack.collide(enemy)) {
                enemy.setHP(attack.getDmg());
            }
            if(attack.collide(boss)) {
                boss.setHP(attack.getDmg());
            }
        }
        for(Attack attack:enemyAttacks) {
            if(attack.collide(player)) {
                player.setHP(attack.getDmg());
            }
        }

        dialogue.update(cameraX);
    }

    public void checkCameraBounds() {
        if(cameraX < 0) {
            cameraX = 0;
        }
        if(cameraX > tileMap.getWidth() - Game.WIDTH) {
            cameraX = tileMap.getWidth() - Game.WIDTH;
        }
    }

    public void draw(Graphics2D g) {
        AffineTransform transform = new AffineTransform();
        transform.setToScale(3,3);
        transform.translate(-cameraX,0);

        g.setTransform(transform);

        background.draw(g);
        tileMap.draw(g);

        enemy.draw(g);
        boss.draw(g);
        player.draw(g);
        for(Attack attack:playerAttacks) {
            attack.draw(g);
        }
        for(Attack attack:enemyAttacks) {
            attack.draw(g);
        }

        dialogue.draw(g);
        //map.draw(g);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_A || k == KeyEvent.VK_D) {
            dialogue.moveToNext(0);
        }
        if(k == KeyEvent.VK_W) {
            dialogue.moveToNext(1);
        }
        if(k == KeyEvent.VK_K) {
            dialogue.moveToNext(4);
        }
        player.keyPressed(k);
    }
    public void keyReleased(int k) {
        player.keyReleased(k);
    }
}