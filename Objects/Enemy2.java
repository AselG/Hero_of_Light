package Objects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;


// Enemy class for melee based enemies with movement
public class Enemy2 {
    // position
    private double x, y, width, height;
    private double vx, vy;
    private double sWidth, sHeight; // sprite width, height
    private int moveSpeed;

    private double hp = 100;
    private double maxHP = 100;

    // resources
    private Image image;
    private static Image[][] images;
    private String resourceDir;

    // actions
    public static final int IDLE = 0;
    public static final int WALK = 1;
    public static final int ATTACK = 2;
    public static final int HIT = 3;

    private String facing = "right";
    private int facing2 = 1;

    public static final int RIGHT = 1;
    public static final int LEFT = -1;

    private Attack attackMove;
    private int attackTimer = 0;
    private int attackFrame = 1;
    private int stunTimer = 0;

    // animations
    private int action = IDLE;
    private boolean actionLock = false;
    private double frame = 0;
    private int[] frames = {9,6,6,3};
    private static int[] sframes = {9,6,6,3};
    private double[] intervals = {0.5,0.5,0.3,0.3};
    private static String[] actionNames = {"idle", "walk", "attack", "hit"};


    public Enemy2(String res, int x, int y, int w, int h, int moveSpeed, int hp) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        this.moveSpeed = moveSpeed;
        this.hp = hp;
        this.maxHP = hp;
        resourceDir = res;
        init();

    }

    public void init() {
        image = new ImageIcon(resourceDir+"idle01.png").getImage();
        image = image.getScaledInstance(32,32, Image.SCALE_DEFAULT);
        sWidth = 40;
        sHeight = 40;
    }

    public static void loadImages(String resourceDir) {
        images = new Image[actionNames.length][];
        for(int i=0; i<actionNames.length; i++) {
            images[i] = new Image[sframes[i]];
            for(int j=0; j<sframes[i]; j++) {
                images[i][j] = new ImageIcon(resourceDir+actionNames[i]+"0"+(j+1)+".png").getImage();
                images[i][j] = images[i][j].getScaledInstance(40,40, Image.SCALE_DEFAULT);
            }
        }
    }

    public void update(Map map, Player player) {
        if(Math.abs(x - player.getX()) < 330 && hp>0) {
            attackMove = null;

            checkAction(map, player);
            horizontalMove(map);
            verticalMove(map);
            updateFrame();
        }
    }

    public void checkAction(Map map, Player player) {
        if(!collideMap(map.getPolygon(), (int)x, (int)(y+1))) {                                                         // only updates if alive and if visible to player
            vy = Math.min(vy+2, 8);
        }
        double px = player.getX();
        double py = player.getY();
        if(!actionLock && Math.hypot(x-px,0) > 20) {
            if(px > x) {
                facing = "right";
                facing2 = RIGHT;
                action = WALK;
                vx = 3;
            }
            else{
                facing = "left";
                facing2 = LEFT;
                action = WALK;
                vx = -3;
            }
        }
        if(Math.abs((int)(px - x)) < 40 && Math.abs((int)(py - y)) < 10 && attackTimer==0) {                            // attacks if player is within certain range
            if(action!=ATTACK){
                frame = 0;
            }
            action = ATTACK;
            actionLock = true;
        }
        else if(!actionLock){
            action = IDLE;
        }

        if(action!=WALK) {
            vx*=0.6;
        }
        if(action == ATTACK && frame >= attackFrame && frame<attackFrame+intervals[action]) {
            setAttackMove(x + (facing2*10), y + 5, 20, 10);
        }

        if(attackTimer > 0) {
            attackTimer--;
        }

        if(stunTimer > 0) {
            stunTimer--;
        }
    }

    public void setAttackMove(double x, double y, int w, int h) {
        attackMove = new Attack(x, y, (int)w, (int)h, 10, 2);
    }
    public Attack getAttackMove() { return attackMove; }

    public void updateFrame() {
        if(frame>frames[action]-1) {
            actionLock = false;
            frame = 0;
            if(action == ATTACK) {
                attackTimer = 30;
            }
        }
        else {
            frame += intervals[action];
        }
    }

    public void horizontalMove(Map map) {
        x = x + vx;
        if(collideMap(map.getPolygon(), (int)x, (int)y)) {
            while(!collideMap(map.getPolygon(), (int)x, (int)y)) {
                x += posNeg(vx);
            }
        }
        else if(!collideMap(map.getPolygon(), (int)(x+(posNeg(vx)*width/2)), (int)(y+1))) {
            x -= vx;
        }
    }
    public void verticalMove(Map map) {
        y = y + vy;
        if(collideMap(map.getPolygon(), (int)x, (int)y)) {
            y -= vy;
            while(!collideMap(map.getPolygon(), (int)x, (int)y)) {
                y += posNeg(vy);
            }
            y -= posNeg(vy);
            vy = 0;
        }
    }

    public int posNeg(double val){
        if(val>=1){ return 1; }
        else if(val<=-1){ return -1; }
        else{ return 0; }
    }

    public boolean collideMap(Polygon[] polygons, int x, int y) {
        boolean collision = false;
        double[] corners = generateCorners(x, y);

        for(Polygon poly:polygons){
            for(int i=0; i<corners.length/2; i++){
                if(poly.contains(corners[i*2],corners[i*2+1])) { collision = true; }
            }
        }

        return collision;
    }

    public double[] generateCorners(double x, double y) {
        double[] corners = new double[8];
        corners[0] = x - width/2;
        corners[1] = y - height/2 + 3;
        corners[2] = x + width/2;
        corners[3] = y - height/2 + 3;
        corners[4] = x + width/2;
        corners[5] = y + height/2;
        corners[6] = x - width/2;
        corners[7] = y + height/2;

        return corners;
    }

    public void setHP(int dmg) {
        hp -= dmg;
        if(stunTimer==0) {
            System.out.println("stunned");
            action = HIT;
            actionLock = true;
            stunTimer = 25;
            //attackTimer = 10;
            frame = 0;
            vx = 5;
        }
    }
    public double getHP() { return hp; }
    public Rectangle getHitRect() {
        Rectangle rect = new Rectangle((int)(x-width/2), (int)(y-height/2), (int)width, (int)height);
        return rect;
    }

    public void draw(Graphics2D g){
        if(hp>0) {
            Image pic = images[action][(int) frame];
            if (facing == "right") {
                g.drawImage(pic, (int) (x - sWidth / 2) + 5, (int) (y - sHeight / 2), null);
            } else {
                g.drawImage(pic, (int) (x - sWidth / 2) + (int) sWidth - 5, (int) (y - sHeight / 2), (int) -sWidth, (int) sHeight, null);
            }

            // collision box
            g.setColor(Color.WHITE);

            double[] corners = generateCorners(x, y);
            g.drawLine((int) corners[0], (int) corners[1], (int) corners[2], (int) corners[3]);
            g.drawLine((int) corners[2], (int) corners[3], (int) corners[4], (int) corners[5]);
            g.drawLine((int) corners[4], (int) corners[5], (int) corners[6], (int) corners[7]);
            g.drawLine((int) corners[6], (int) corners[7], (int) corners[0], (int) corners[1]);


            drawHPbar(g);
        }
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

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }

}
