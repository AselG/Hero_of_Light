package Objects;

import GameState.GameState;
import GameState.PlayState;
import Main.Game;

import javax.swing.*;
import java.awt.*;

// Class for stationary enemies
// can be normal enemy, or boss type enemy
public class Enemy1 {

    // position
    private double x, y, width, height;
    private double sWidth, sHeight; // sprite width, height

    // combat
    private double hp;
    private double maxHP;
    private int state;
    public static final int ALIVE = 0;
    public static final int DEAD = 1;

    private int type;
    public static final int NORMAL = 0;
    public static final int BOSS = 1;

    // resources
    private Image image;
    private static Image[][] images;
    private String resourceDir;

    // actions
    public static final int IDLE = 0;
    public static final int ATTACK = 1;
    public static final int HIT = 2;

    private String facing = "right";
    private int facing2 = 1;

    public static final int RIGHT = 1;
    public static final int LEFT = -1;

    private Attack attackMove;
    private int dmg;
    private int attackFrame = 8;
    private int stunTimer = 0;

    // animations
    private int action = IDLE;
    private boolean actionLock = false;
    private double frame = 0;
    private int[] frames = {8,11,3};
    private static int[] sframes = {8,11,3};
    private double[] intervals = {0.5,0.5,0.5};
    private static String[] actionNames = {"idle", "attack", "hit"};


    public Enemy1(String res, int x, int y, int w, int h, int type, double hp) {

        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.hp = hp;
        maxHP = hp;
        this.type = type;
        resourceDir = res;
        init();
        if(type == BOSS) {
            scaleImages();
        }
        dmg = 10 + (10*type);
    }

    public void init() {
        image = new ImageIcon(resourceDir+"idle01.png").getImage();
        sWidth = image.getWidth(null);
        sHeight = image.getHeight(null);
    }

    public static void loadImages(String resourceDir) {
        images = new Image[actionNames.length][];
        for(int i=0; i<actionNames.length; i++) {
            images[i] = new Image[sframes[i]];
            for(int j=0; j<sframes[i]; j++) {
                images[i][j] = new ImageIcon(resourceDir+actionNames[i]+"0"+(j+1)+".png").getImage();
            }
        }
    }
    // increases image size and relevant dimensions if enemy is a boss
    public void scaleImages() {
        sWidth = sWidth*1.5;
        sHeight = sHeight*1.5;
        width = width*1.5;
        height = height*1.5;
        for(int i=0; i<actionNames.length; i++) {
            for(int j=0; j<frames[i]; j++) {
                if(type == BOSS) {
                    images[i][j] = images[i][j].getScaledInstance((int)sWidth,(int)sHeight, Image.SCALE_DEFAULT);
                }
            }
        }
    }

    public void update(Map map, Player player) {
        if(state!=DEAD) {
            attackMove = null;
            checkAction(player);
            updateFrame();
        }

        if(hp <= 0) {
            state = DEAD;
        }
    }

    public void checkAction(Player player) {
        double px = player.getX();
        double py = player.getY();
        if(!actionLock) {
            if(px > x) {                                                                                                // facing direction depends on players x value
                facing = "right";
                facing2 = RIGHT;
            }
            else{
                facing = "left";
                facing2 = LEFT;
            }
        }
        if(Math.abs((int)(px - x)) < 40 && Math.abs((int)(py - y)) < 20 && !actionLock) {                               // action set to attack if player is within certain distance
            if(action!=ATTACK){                                                                                         // specifically set to not notice player to as far a distance if above
                frame = 0;
            }
            action = ATTACK;
            actionLock = true;                                                                                          // lock animation so enemy cant switch direction of attack
        }
        else if(!actionLock){
            action = IDLE;
        }

        if(action == ATTACK && (int)frame==attackFrame) {
            setAttackMove(x + (facing2*20), y + 10, 25*(type+1), 18*(type+1));
        }

        if(stunTimer>0) {
            stunTimer--;
        }
    }

    public void setAttackMove(double x, double y, int w, int h) {
        attackMove = new Attack(x, y, (int)w, (int)h, dmg, 1);
    }
    public Attack getAttackMove() { return attackMove; }

    public void updateFrame() {
        if(frame>frames[action]-1) {
            actionLock = false;
            frame = 0;
        }
        else {
            frame += intervals[action];
        }
    }

    public void setHP(int dmg) {
        hp -= dmg;
        if(stunTimer == 0){                                                                                             // enemy is stunned if hit by attack,
            action = HIT;                                                                                               // after being stunned, cannot be stunned again until stun timer resets
            frame = 0;
            stunTimer = 40;
            actionLock = true;
        }
    }
    public double getHP() { return hp; }
    public Rectangle getHitRect() {
        Rectangle rect = new Rectangle((int)(x-width/2), (int)(y-height/2), (int)width, (int)height);
        return rect;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getW() { return width; }
    public double getH() { return height; }
    public int getState() { return state; }

    public void draw(Graphics2D g){
        if(state != DEAD) {
            Image pic = images[action][(int)frame];
            if(facing == "right"){
                g.drawImage(pic, (int)(x - sWidth/2), (int)(y - sHeight/2) - 15, (int) sWidth, (int) sHeight, null);
            }
            else{
                g.drawImage(pic, (int)(x - sWidth/2) + (int)sWidth, (int)(y - sHeight/2) - 15, (int)-sWidth, (int)sHeight, null);

            }
            drawHPbar(g);
        }

        g.setColor(Color.WHITE);
    }

    public void drawHPbar(Graphics2D g) {
        int rectW = 32;
        int rectH = 5;
        double hpW = hp/maxHP * (rectW-2);
        g.setColor(Color.BLACK);
        g.fillRect((int)x-rectW/2,(int)y-30, rectW, rectH);
        g.setColor(Color.RED);
        g.fillRect((int)x-(rectW/2-1),(int)y-29, (int)hpW, rectH-2);
    }

}
