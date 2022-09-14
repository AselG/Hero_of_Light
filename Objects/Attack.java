package Objects;


import java.awt.*;

// Attack class, used to check for collision with player or enemies
public class Attack {
    // Position
    private double x, y;
    private int w, h, dmg;
    private int caster;

    // Constants for caster
    public static final int PLAYER = 0;
    public static final int ENEMY1 = 1;
    public static final int ENEMY2 = 2;
    public static final int BOSS = 3;

    public Attack(double x, double y, int w, int h, int dmg, int caster) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.dmg = dmg;
        this.caster = caster;

    }

    public Rectangle getHitRect() {
        Rectangle rect = new Rectangle((int)x-w/2, (int)y-h/2, w, h);
        return rect;
    }

    // collide method player and each enemy type
    // gets the hitbox of the attack and the entity, checks if they're intersecting
    public boolean collide(Player player) {
        Rectangle rect = getHitRect();
        Rectangle pRect = player.getHitRect();
        return rect.intersects(pRect);
    }
    public boolean collide(Enemy1 enemy) {
        Rectangle rect = getHitRect();
        Rectangle eRect = enemy.getHitRect();
        return rect.intersects(eRect);
    }
    public boolean collide(Enemy2 enemy) {
        Rectangle rect = getHitRect();
        Rectangle eRect = enemy.getHitRect();
        return rect.intersects(eRect);
    }
    public boolean collide(Boss1 enemy) {
        Rectangle rect = getHitRect();
        Rectangle eRect = enemy.getHitRect();
        return rect.intersects(eRect);
    }

    // Getters
    public int getDmg() { return dmg; }

    public void draw(Graphics2D g) {
        Rectangle rect = getHitRect();
        g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
    }

}
