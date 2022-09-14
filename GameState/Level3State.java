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

public class Level3State extends GameState {

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
    public Boss1 boss2;

    // Attacks
    public ArrayList<Attack> playerAttacks = new ArrayList<Attack>();
    public ArrayList<Attack> enemyAttacks = new ArrayList<Attack>();

    private Image [] back;

    public Level3State(StateMachine stateMachine) throws IOException {
        this.stateMachine = stateMachine;


    }

    public void init() {

        Enemy1.loadImages("Resources/Enemy/Enemy03/");
        Enemy2.loadImages("Resources/Enemy/Enemy05/");

        try {
            tileMap = new TileMap("Resources/level3/map.txt");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        background = new Background("Resources/backgrounds/background", 6, 2,1, cameraX);

        map = new Map("Resources/level3/mapPoly.txt");
        player = new Player(map);
        //spells.add(new Spell(player.getX(), player.getY(), 0));
        boss1 = new Enemy1("Resources/Enemy/Enemy03/", 1040,195, 30, 35, 1, 300);
        boss2 = new Boss1("Resources/Enemy/Boss1/",1770,200,30,50,500);
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 645,250, 30, 35, 0, 40));
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 890,238, 30, 35, 0, 80));
        enemies.add(new Enemy1("Resources/Enemy/Enemy03/", 1172,255, 30, 35, 0, 80));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 900,310, 15,35,3,30));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 1010,310, 15,35,3,30));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 1065,310, 15,28,3,50));
        enemies2.add(new Enemy2("Resources/Enemy/Enemy05/", 1005,200, 15,28,3,30));

        Spell.spell1Load();
        Spell.spell2Load();
        Spell loadspell1 = new Spell(-100,1000,0, player.getFacing());
        spells.add(loadspell1);
        Spell loadspell2 = new Spell(-100,1000, 1, player.getFacing());
        spells.add(loadspell2);
        dialogue = new Dialogue("Resources/level3/Dialogue.txt");
    }
    public void update() {
        if(player.getHP() <= 0) {
            stateMachine.setState(StateMachine.LEVELFAILEDSTATE);
        }
        if(dialogue.getCurrent() == 14) {
            player.setMana(50);
        }
        if(player.getX() >= 895) {
            dialogue.called(13);
        }
        if(boss1.getState() == Enemy1.DEAD) {
            dialogue.called(18);
        }
        if(player.getX() >= 1560) {
            dialogue.called(19);
        }
        if(boss2.getState() == Boss1.DEAD) {
            dialogue.called(26);
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
        boss1.update(map, player);
        if(boss1.getAttackMove() != null) {
            enemyAttacks.add(boss1.getAttackMove());
        }
        boss2.update(map, player);
        if(boss2.getAttackMove() != null) {
            enemyAttacks.add(boss2.getAttackMove());
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
                    if(boss1.getHP()<=120) {
                        dialogue.called(17);
                    }
                }
                else {
                    spell = new Spell(boss2.getX(),boss2.getY()+20,0, player.getFacing());
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
        transform.setToScale(3,3);
        transform.translate(-cameraX,0);

        g.setTransform(transform);

        background.draw(g);
        tileMap.draw(g);

        for(Spell spell:spells) {
            spell.draw(g);
        }
        boss1.draw(g);
        boss2.draw(g);
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
        if(k == KeyEvent.VK_L) {
            dialogue.moveToNext(15);
        }
        player.keyPressed(k);
    }
    public void keyReleased(int k) {
        player.keyReleased(k);
    }
}
