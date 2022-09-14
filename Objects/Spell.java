package Objects;

import javax.swing.*;
import java.awt.*;

// Spell class, contains position and resources for two spell types
// used to check for collision with entities
public class Spell {
    // Position
    private double x,y;
    private int sw,sh,w,h;                                                                                              // w,h - width and height of hitbox
                                                                                                                        // sw, sh - width and height of images (s for sprite)

    private int direction;
    // Constants for direction
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    // Type
    private int type;
    public static final int SPELL1 = 0;
    public static final int SPELL2 = 1;
    // Animation
    private double frame;
    private double frames = 9;
    private double interval = 1;
    private int dmgFrame = 5;
    private static Image[] images1;
    private static Image[] images2;
    // Damage
    private int dmg;
    // True if animation is still active, false if animation complete
    private boolean active;

    public Spell(double x, double y, int t, int facing) {
        this.x = x;
        this.y = y;
        direction = facing;
        type = t;
        active = true;
        if(type == SPELL1) {
            spell1init();
        }
        else{
            spell2init();
        }
    }
    // Loads resources for spell 1
    public static void spell1Load() {
        images1 = new Image[10];
        for(int i=0; i<images1.length; i++) {
            images1[i] = new ImageIcon("Resources/spells/spell1/Lightning-bolt"+i+"0.png").getImage();
            images1[i] = images1[i].getScaledInstance(100,350,Image.SCALE_DEFAULT);
        }
    }
    // Loads resources for spell 2
    public static void spell2Load() {
        images2 = new Image[10];
        for(int i=0; i<images2.length; i++) {
            images2[i] = new ImageIcon("Resources/spells/spell2/Lightning"+i+"0.png").getImage();
            images2[i] = images2[i].getScaledInstance(50,50,Image.SCALE_DEFAULT);
        }
    }
    // Initilaizes spell 1
    public void spell1init() {
        sw = 100;                                                                                                       // sets w,h of hitbox and w,h of images
        sh = 100;
        w = 40;
        h = 80;
        frame = 0;
        dmg = 120;
    }
    // Initializes spell 2
    public void spell2init() {
        sw = 50;
        sh = 50;
        w = 50;
        h = 15;
        frame = 0;
        dmg = 30;
    }
    public void update() {
        if(frame>frames-1) {                                                                                            // frame updated
            active = false;
        }
        else {
            frame += interval;
        }

    }

    public Rectangle getHitRect() {                                                                                     // returns rectangle of spell with target at x,y
        Rectangle rect = null;
        if(type == SPELL1) {                                                                                            // spell1's hitbox goes from top of screen to y
            rect = new Rectangle((int)x-w/2, 0, w, (int)y);
        }
        if(type == SPELL2) {                                                                                            // spell2's hitbox is rectangle going out to left or right from x position
            if(direction==RIGHT) {
                rect = new Rectangle((int)x, (int)(y-h/2), w, h);
            }
            else {
                rect = new Rectangle((int)x-w, (int)(y-h/2), w, h);                                                  // image reflected if spell is being cast to the left
            }
        }
        return rect;
    }
    // Check for collision with all enemy types (only player can cast spells)
    public boolean collide(Boss1 enemy) {
        if((int)frame == dmgFrame) {
            Rectangle rect = getHitRect();
            Rectangle eRect = enemy.getHitRect();
            return rect.intersects(eRect);
        }
        return false;
    }
    public boolean collide(Enemy1 enemy) {
        if((int)frame == dmgFrame) {
            Rectangle rect = getHitRect();
            Rectangle eRect = enemy.getHitRect();
            return rect.intersects(eRect);
        }
        return false;
    }
    public boolean collide(Enemy2 enemy) {
        if((int)frame == dmgFrame) {
            Rectangle rect = getHitRect();
            Rectangle eRect = enemy.getHitRect();
            return rect.intersects(eRect);
        }
        return false;
    }

    public void draw(Graphics2D g) {
        if(type == SPELL1) {
            Image pic = images1[(int)frame];
            g.drawImage(pic, (int)(x-sw/2), (int)y-350, null);
            g.drawRect((int)x-w/2, 0, w, (int)y);
        }
        if(type == SPELL2) {
            Image pic = images2[(int)frame];
            g.drawImage(pic, (int)(x) + direction*5, (int)(y-sh/2) - 3, direction*sw, sh, null);
            g.drawRect((int)x, (int)(y-h/2), w*direction, h);
        }
    }

    // Getters
    public boolean getActive() { return active; }
    public int getDmg() { return dmg; }
    public boolean getDmgActive() { return frame == dmgFrame; }

}
