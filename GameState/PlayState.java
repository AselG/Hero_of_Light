package GameState;

import Main.Game;
import Objects.*;
import TileMap.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;

public class PlayState extends GameState{

    private double cameraX = 0;
    private double cameraY = 0;

    private TileMap tileMap;
    private Background background;

    // Objects
    public Map map;
    public Player player;
    public ArrayList<Spell> spells = new ArrayList<Spell>();
    public ArrayList<Enemy1> enemies = new ArrayList<Enemy1>();
    public ArrayList<Enemy2> enemies2 = new ArrayList<Enemy2>();
    public Boss1 boss = new Boss1("Resources/Enemy/Boss1/png/",500,200,30,50, 100);

    // Attacks
    public ArrayList<Attack> playerAttacks = new ArrayList<Attack>();
    public ArrayList<Attack> enemyAttacks = new ArrayList<Attack>();

    private Image [] back;

    public PlayState(StateMachine stateMachine) throws IOException {
        this.stateMachine = stateMachine;

        tileMap = new TileMap("Resources/map1.txt");
        background = new Background("Resources/backgrounds/background", 6, 2,1, cameraX);

        back = new Image[6];
        for(int i=0; i<back.length; i++){
            back[i] = new ImageIcon("Resources/Backgrounds/background"+(i+1)+".png").getImage();
            back[i] = back[i].getScaledInstance(640, 400, Image.SCALE_DEFAULT);
        }
        //map = new ImageIcon("Resources/map1.png").getImage();
        //map = map.getScaledInstance(960, 640, Image.SCALE_DEFAULT);

        map = new Map("Resources/map1Poly2.txt");
        player = new Player(map);
        //spells.add(new Spell(player.getX(), player.getY(), 0));
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 180,367, 30, 35, 0, 20));
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 328,168, 30, 35, 0, 20));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 140,100, 15,35,3,20));
    }


    public void init(){

    }
    public void update() {
        playerAttacks.clear();
        enemyAttacks.clear();

        cameraX = player.getX() - Game.WIDTH/2;
        checkCameraBounds();

        background.update(cameraX);

        player.update();
        ArrayList<Spell> remSpells = new ArrayList<Spell>();
        for(Spell spell:spells) {
            if(!spell.getActive()) {
                remSpells.add(spell);
            }
            spell.update();
        }
        spells.removeAll(remSpells);

        //tileMap.setPosition(-(Game.WIDTH/2 - player.getX()),0);

        ArrayList<Enemy1> remEnemy1 = new ArrayList<Enemy1>();
        ArrayList<Enemy2> remEnemy2 = new ArrayList<Enemy2>();
        for(Enemy1 enemy:enemies) {
            enemy.update(map, player);
            if(enemy.getHP() <= 0) {
                remEnemy1.add(enemy);
            }
            if(enemy.getAttackMove() != null) {
                enemyAttacks.add(enemy.getAttackMove());
            }
        }
        enemies.removeAll(remEnemy1);
        for(Enemy2 enemy:enemies2) {
            enemy.update(map, player);
            if(enemy.getHP() <= 0) {
                remEnemy2.add(enemy);
            }
            if(enemy.getAttackMove() != null) {
                enemyAttacks.add(enemy.getAttackMove());
            }
        }
        enemies2.removeAll(remEnemy2);
        boss.update(map, player);
        if(boss.getAttackMove() != null) {
            enemyAttacks.add(boss.getAttackMove());
        }

        if(player.getAttackMove() != null) {
            playerAttacks.add(player.getAttackMove());
        }
        if(player.getSpellMove() != -1) {
            //Spell spell;
            if(player.getSpellMove() == 0) {
                Spell spell = new Spell(boss.getX(),boss.getY(),0, player.getFacing());
                spells.add(spell);
            }
            else if(player.getSpellMove() == 1) {
                Spell spell = new Spell(player.getX(), player.getY(), 1, player.getFacing());
                spells.add(spell);
            }
        }
        for(Spell spell:spells) {
            if(spell.collide(boss) && spell.getDmgActive()) {
                boss.setHP(spell.getDmg());
                System.out.println(boss.getHP());
            }
        }

        for(Attack attack:playerAttacks) {
            for(Enemy1 enemy:enemies) {
                if(attack.collide(enemy)) {
                    enemy.setHP(attack.getDmg());
                }
            }
            for(Enemy2 enemy:enemies2) {
                if(attack.collide(enemy)) {
                    enemy.setHP(attack.getDmg());
                    System.out.println(enemy.getHP());
                }
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
        AffineTransform transform1 = new AffineTransform();
        transform.setToScale(3,3);
        //transform1.setToScale(3,3);
        transform.translate(-cameraX,0);

        //g.setTransform(transform1);

        //for(int i=0; i<back.length; i++){
        //    g.drawImage(back[i], 0, 0, null);
        //}

        g.setTransform(transform);

        background.draw(g);

        //long before = System.nanoTime();

        tileMap.draw(g);
        //long after = System.nanoTime();
        for(Enemy1 enemy:enemies) {
            enemy.draw(g);
        }
        for(Enemy2 enemy:enemies2) {
            enemy.draw(g);
        }
        boss.draw(g);
        player.draw(g);
        for(Spell spell:spells) {
            spell.draw(g);
        }
        for(Attack attack:playerAttacks) {
            attack.draw(g);
        }
        for(Attack attack:enemyAttacks) {
            attack.draw(g);
        }
        //System.out.println((after-before)/1000000);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_LEFT){
            //tileMap.setPosition(tileMap.getX()-4,0);
        }
        if(k == KeyEvent.VK_RIGHT){
            //tileMap.setPosition(tileMap.getX()+4,0);
        }
        player.keyPressed(k);
    }
    public void keyReleased(int k) {
        player.keyReleased(k);
    }

    //public static Map getMap() { return map; }
}
