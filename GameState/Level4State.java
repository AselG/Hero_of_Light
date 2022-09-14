package GameState;

import GameState.GameState;
import Main.Game;
import Objects.*;
import TileMap.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;

// Same structure as level 1, dialogue trigger events are different, enemies stored in array lists
public class Level4State extends GameState {

    private double cameraX = 0;
    private double cameraY = 0;

    private TileMap tileMap;
    private Background background;
    public Dialogue dialogue;

    // Objects
    public Map map;
    public Player player;
    public ArrayList<Spell> spells = new ArrayList<Spell>();
    public ArrayList<Enemy1> enemies = new ArrayList<Enemy1>();
    public ArrayList<Enemy2> enemies2 = new ArrayList<Enemy2>();
    public Enemy1 boss1;
    public Enemy1 boss2;
    public Boss1 boss3;

    // Attacks
    public ArrayList<Attack> playerAttacks = new ArrayList<Attack>();
    public ArrayList<Attack> enemyAttacks = new ArrayList<Attack>();

    private Image [] back;

    public Level4State(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void init() {
        Enemy1.loadImages("Resources/Enemy/Enemy03/");
        Enemy2.loadImages("Resources/Enemy/Enemy05/");
        try {
            tileMap = new TileMap("Resources/level4/map2.txt");
            background = new Background("Resources/backgrounds/background", 6, 2, 1, cameraX);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        map = new Map("Resources/level4/mapPoly.txt");
        player = new Player(map);
        player.setMana(50);
        //spells.add(new Spell(player.getX(), player.getY(), 0));
        boss1 = new Enemy1("Resources/Enemy/Enemy03/", 865,195, 30, 35, 1, 300);
        boss2 = new Enemy1("Resources/Enemy/Enemy03/", 1160,70, 30, 35, 1, 300);
        boss3 = new Boss1("Resources/Enemy/Boss1/",1000,285,30,50,1000);
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 1015,255, 30, 35, 0, 100));
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 765,320, 30, 35, 0, 60));
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 1175,320, 30, 35, 0, 60));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 885,300, 15,35,3,40));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 1095,300, 15,35,3,40));

        Spell.spell1Load();
        Spell.spell2Load();
        Spell loadspell1 = new Spell(-100,1000,0, player.getFacing());
        spells.add(loadspell1);
        Spell loadspell2 = new Spell(-100,1000, 1, player.getFacing());
        spells.add(loadspell2);

        dialogue = new Dialogue("Resources/Level4/Dialogue.txt");
    }
    public void update() {
        if(player.getHP() <= 0) {
            stateMachine.setState(StateMachine.LEVELFAILEDSTATE);
        }

        playerAttacks.clear();
        enemyAttacks.clear();

        cameraX = player.getX() - Game.WIDTH/2;
        checkCameraBounds();

        background.update(cameraX);

        player.update();

        // check if spells are still active
        ArrayList<Spell> remSpells = new ArrayList<Spell>();
        for(Spell spell:spells) {
            if(!spell.getActive()) {
                remSpells.add(spell);
            }
            spell.update();
        }
        spells.removeAll(remSpells);

        // lists to hold enemies which will be removed
        ArrayList<Enemy1> remEnemy1 = new ArrayList<Enemy1>();
        ArrayList<Enemy2> remEnemy2 = new ArrayList<Enemy2>();
        // update all attack/spell moves, remove enemies with hp below 0
        if(boss1.getState()!=Enemy1.DEAD) {
            boss1.update(map, player);
            if(boss1.getAttackMove() != null) {
                enemyAttacks.add(boss1.getAttackMove());
            }
        }
        if(boss2.getState()!=Enemy1.DEAD) {
            boss2.update(map, player);
            if (boss2.getAttackMove() != null) {
                enemyAttacks.add(boss2.getAttackMove());
            }
        }
        if(boss3.getState()!=Boss1.DEAD) {
            boss3.update(map, player);
            if (boss3.getAttackMove() != null) {
                enemyAttacks.add(boss3.getAttackMove());
            }
        }
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

        if(player.getAttackMove() != null) {
            playerAttacks.add(player.getAttackMove());
        }
        if(player.getSpellMove() != -1) {
            if(player.getSpellMove() == 0) {
                Spell spell = null;
                if(boss1.getHP() > 0) {
                    spell = new Spell(boss1.getX(),boss1.getY() + boss1.getH()/2,0, player.getFacing());
                }
                else if(boss2.getHP() > 0){
                    spell = new Spell(boss2.getX(),boss2.getY()+20,0, player.getFacing());
                }
                else {
                    spell = new Spell(boss3.getX(),boss3.getY()+20,0, player.getFacing());
                    if(boss3.getHP() <= 120) {
                        dialogue.called(10);
                    }
                }
                spells.add(spell);
            }
            else if(player.getSpellMove() == 1) {
                Spell spell = new Spell(player.getX(), player.getY(), 1, player.getFacing());
                spells.add(spell);
            }
        }
        for(Spell spell:spells) {
            for(Enemy1 enemy: enemies) {
                if(spell.collide(enemy) && spell.getDmgActive()) {
                    enemy.setHP(spell.getDmg());
                }
            }
            for(Enemy2 enemy: enemies2) {
                if(spell.collide(enemy) && spell.getDmgActive()) {
                    enemy.setHP(spell.getDmg());
                }
            }
            if(spell.collide(boss1) && spell.getDmgActive()) {
                boss1.setHP(spell.getDmg());
            }
            if(spell.collide(boss2) && spell.getDmgActive()) {
                boss2.setHP(spell.getDmg());
            }
            if(spell.collide(boss3) && spell.getDmgActive()) {
                boss3.setHP(spell.getDmg());
            }
        }

        // check hit-boxes for each attack
        for(Attack attack:playerAttacks) {
            for(Enemy1 enemy:enemies) {
                if(attack.collide(enemy)) {
                    enemy.setHP(attack.getDmg());
                }
            }
            for(Enemy2 enemy:enemies2) {
                if(attack.collide(enemy)) {
                    enemy.setHP(attack.getDmg());
                }
            }
            if(attack.collide(boss1)) {
                boss1.setHP(attack.getDmg());
            }
            if(attack.collide(boss2)) {
                boss2.setHP(attack.getDmg());
            }
            if(attack.collide(boss3)) {
                boss3.setHP(attack.getDmg());
            }
        }
        for(Attack attack:enemyAttacks) {
            if(attack.collide(player)) {
                player.setHP(attack.getDmg());
            }
        }
        dialogue.update(cameraX);

        if(dialogue.getComplete()) {
            stateMachine.setState(StateMachine.LEVELCOMPLETESTATE);
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
        transform.setToScale(2,2);
        transform.translate(-cameraX,0);

        g.setTransform(transform);

        background.draw(g);
        tileMap.draw(g);

        for(Spell spell:spells) {
            spell.draw(g);
        }
        boss1.draw(g);
        boss2.draw(g);
        boss3.draw(g);
        for(Enemy1 enemy:enemies) {
            enemy.draw(g);
        }
        for(Enemy2 enemy:enemies2) {
            enemy.draw(g);
        }
        player.draw(g);
        for(Attack attack:playerAttacks) {
            attack.draw(g);
        }
        for(Attack attack:enemyAttacks) {
            attack.draw(g);
        }

        dialogue.draw(g);

        player.drawHUD(g, (int)cameraX);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ESCAPE) {
            stateMachine.setState(StateMachine.PAUSESTATE);
        }
        player.keyPressed(k);
    }
    public void keyReleased(int k) {
        player.keyReleased(k);
    }
}