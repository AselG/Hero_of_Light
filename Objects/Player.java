package Objects;

import GameState.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {

    // Position
    private double x, y, width, height;                                                                                 // hitbox width, height
    private double vx, vy;
    private double sWidth, sHeight;                                                                                     // sprite width, height
    // Health and Mana
    private double hp = 100;
    private double maxHP = 100;
    private double mana = 25;
    private double maxMana = 25;
    private int dmg;

    public static final int HEIGHT = 30;

    private Image image;
    private Map map;

    // keys for movement
    private boolean keyLeft = false;
    private boolean keyRight = false;
    private boolean keyUp1 = false;
    private boolean resetKeyUp = false;
    private int keyUp = 0;
    private boolean keyAttack = false;
    private boolean keyCast1 = false;
    private boolean keyCast2 = false;
    private boolean doubleJump = false;

    // Constants for action
    public static final int IDLE = 0;
    public static final int RUN = 1;
    public static final int JUMP = 2;
    public static final int FLIP = 3;
    public static final int FALL = 4;
    public static final int ATTACK1 = 5;
    public static final int ATTACK2 = 6;
    public static final int ATTACK3 = 7;
    public static final int AIRATTACK1 = 8;
    public static final int AIRATTACK2 = 9;
    public static final int AIRATTACK3 = 10;
    public static final int CAST = 11;
    // Indicates facing left or right
    private String facing = "right";                                                                                    // Initially used string
    private int facing2 = 1;                                                                                            // added another variable with uses int instead so I could do math
                                                                                                                        // operations with it
    //Constants for facing2
    public static final int RIGHT = 1;
    public static final int LEFT = -1;
    // Attacks and Spells
    private Attack attackMove;
    private int[] attackFrames = {-1,-1,-1,-1,-1,2,3,2,2,2,-1,-1};
    private int spell;
    private int currentSpellMove;
    private int spellFrame = 2;
    public static final int SPELL1 = 0;
    public static final int SPELL2 = 1;

    // Animation
    private int action = IDLE;                                                                                          // holds current animation
    private boolean actionLock = false;                                                                                 // flag to lock player into an animation
    private boolean airAttack = true;                                                                                   // flag to indicate whether the player has attacked in the air yet
                                                                                                                        // becomes false once the player attacks from the air to prevent player from
                                                                                                                        // being able to indefinitely hover in the air using air attacks
    private double frame = 0;                                                                                           // frame of current action
    private int[] frames = {3, 6, 4, 4, 2, 6, 7, 7, 5, 4, 8, 8};                                                        // holds number of frames for each action
    private double[] intervals = {0.5, 0.5, 0.5, 0.5, 0.5, 0.9, 0.9, 0.9, 0.7, 0.7, 0.4, 0.5};                          // interval to increase frame variable by for each action
    //                                               (smrslt - somersault (flip))
    private String[] actionNames = {"idle", "run", "jump", "smrslt", "fall", "attack1", "attack2", "attack3", "air-attack1", "air-attack2", "air-attack3", "cast"};
                                                                                                                        // contains names of each action in resources folder, used to load images
    private Image[][] images;                                                                                           // 2d array for storing images
                                                                                                                        // first index determines which animation, second determines which frame

    public Player(Map m) {
        // map stored to check for collision when moving
        map = m;
        init();

    }

    public void init() {
        x = 80;
        y = 220;
        vx = 0;
        vy = 0;
        width = 15;
        height = 30;
        // Resources loaded
        image = new ImageIcon("Resources/hero/sprites/adventurer-idle-2-00.png").getImage();
        sWidth = image.getWidth(null);
        sHeight = image.getHeight(null);
        loadImages();
    }
    // Loads all animations
    public void loadImages() {
        String dir = "Resources/hero/sprites/adventurer-";
        images = new Image[actionNames.length][];
        for(int i=0; i<actionNames.length; i++) {
            images[i] = new Image[frames[i]];
            for(int j=0; j<frames[i]; j++) {
                images[i][j] = new ImageIcon(dir+actionNames[i]+"-0"+j+".png").getImage();
            }
        }
    }

    public void update() {
        // resets attackMove and spell
        attackMove = null;
        spell = -1;

        checkAction();
        checkAttack();
        if(action == FLIP) {                                                                                            // adjusts size of hitbox when flipping to make player harder to hit and more mobile
            height = 20;
        }
        else if(height == 20) {                                                                                         // once player is no longer flipping, waits until player is not colliding with
            height = HEIGHT;                                                                                            // anything to go back to normal hitbox size
            if(collideMap(map.getPolygon(), (int)x, (int)y)) {
                height = 20;
            }
        }

        horizontalMove();
        verticalMove();
        updateFrame();
        if(keyUp1) {                                                                                                    // increases keyUp if W is being pressed, so that holding W key doesnt cause
            keyUp = Math.min(keyUp+1, 2);                                                                               // double jump (only jumps if keyUp is 1, if W is held for more than one frame,
                                                                                                                        // it becomes 2, meaning jumps wont be triggered)
        }
        if(resetKeyUp) {                                                                                                // resets keyUp when W is released
            keyUp = 0;
            resetKeyUp = false;
        }
        if(mana<maxMana) {                                                                                              // Slowly regenerate mana
            mana+=0.25;
        }

    }

    // checkAction - determines action based on user input
    public void checkAction() {
        boolean attacking = action == ATTACK1 || action == ATTACK2 || action == ATTACK3 || action == AIRATTACK1 || action == AIRATTACK2 || action == AIRATTACK3;
        if(!keyLeft && !keyRight) {                                                                                     // decreases horizontal velocity if no left or right movement
            vx *= 0.6;
        }
        else if(keyLeft) {
            if(facing == "right"){ vx = 0; }                                                                            // if direction is switch, vx is set to 0 to give a more snappy feel
                                                                                                                        // as opposed to having to wait for vx to go down from negative side to positive
                                                                                                                        // side or vice versa, can go straight to accelerating in given direction
            facing = "left";
            facing2 = LEFT;
            if(attacking) {                                                                                             // lunge mechanic
                if(frame > 4 && frame < 6) {                                                                            // allows player to lunge forward after each ground attack if movement key is pressed
                    vx = -4;
                }
                else{
                    vx *= 0.5;
                }
            }
            else if(!actionLock){                                                                                       // if player isnt locked to another animation, and player is on ground, action is run
                vx = Math.max(vx-1, -8);
                if(collideMap(map.getPolygon(), (int)x, (int)(y+1))) {
                    action = RUN;
                }
            }
        }
        else if(keyRight) {                                                                                             // same as keyleft, all signs swapped
            if(facing == "left"){ vx = 0; }
            facing = "right";
            facing2 = RIGHT;
            if(attacking) {
                if(frame > 4 && frame < 6) {
                    vx = 4;
                }
                else {
                    vx *= 0.5;
                }
            }
            else if(!actionLock) {
                vx = Math.min(vx+1, 8);
                if(collideMap(map.getPolygon(), (int)x, (int)(y+1))) {
                    action = RUN;
                }
            }
        }
        if (!collideMap(map.getPolygon(), (int) x, (int) (y + 1)) && attacking) {
            if(action == AIRATTACK3) {
                vy = Math.min(vy+6, 20);                                                                                // rapidly increase vy during ground slam attack
                dmg += 2;                                                                                               // damage of attack increases the longer the player falls
            }
            else{                                                                                                       // otherwise the player falls very slowly while attacking in the air
                vy = 1;
            }
            if(keyLeft) {                                                                                               // able to drift left and right while attacking in air
                vx = -3;
            }
            if(keyRight) {
                vx = 3;
            }
        }
        else if (!collideMap(map.getPolygon(), (int) x, (int) (y + 1))) {                                               // if not attacking, player is just falling and vy is increased moderately quickly
            vy = Math.min(vy+2, 18);
        }
        if(!actionLock) {                                                                                               // checks all other movement only if player isnt locked in animation
            if (!collideMap(map.getPolygon(), (int) x, (int) (y + 1))) {
                if (keyUp == 1 && doubleJump) {
                    vy = -11;
                    if (keyRight) {
                        vx = 8;
                        action = FLIP;
                    } else if (keyLeft) {
                        vx = -8;
                        action = FLIP;
                    } else {
                        action = JUMP;
                    }
                    doubleJump = false;                                                                                 // flag for double jump is set to true when player first jumps, then false when
                                                                                                                        // player uses double jump (prevents player from double jumping indefinitely)
                } else if (vy > 0) {
                    action = FALL;
                }
            } else if (!keyLeft && !keyRight) {
                action = IDLE;
            }

            if (collideMap(map.getPolygon(), (int) x, (int) (y + 1))) {                                                 // checks if player is on ground
                doubleJump = false;
                if (keyUp == 1) {                                                                                       // only jumps if keyUp is 1 (keyUp increases as W is held, so holding W doesnt
                    vy = -11;                                                                                           // cause double jump)
                    action = JUMP;
                    doubleJump = true;
                    airAttack = true;
                }
            }
            if (keyCast1 && mana>=50) {                                                                                 // cast spell 1
                action = CAST;
                frame = 0;
                actionLock = true;
            }
            else if(keyCast2 && mana>=20) {                                                                             // cast spell 2
                action = CAST;
                frame = 0;
                actionLock = true;
            }
        }
    }

    // checkAttack - either starts attack, or continues attack sequence based on input
    // also sets the attackMove of player and sets damage and position of attackMove depending on attack
    public void checkAttack() {
        boolean attacking = action == ATTACK1 || action == ATTACK2 || action == ATTACK3 || action == AIRATTACK1 || action == AIRATTACK2 || action == AIRATTACK3;
        if (!attacking) {                                                                                               // if not previously attacking, starts air/ground sequence depending on collision
            if (keyAttack) {
                if(collideMap(map.getPolygon(), (int) x, (int) (y + 1))) {
                    action = ATTACK1;
                    frame = 0;
                }
                if(!collideMap(map.getPolygon(), (int) x, (int) (y + 1)) && airAttack) {
                    action = AIRATTACK1;
                    airAttack = false;
                    frame = 0;
                }
                actionLock = true;                                                                                      // locks animation for that first attack animation
            }
        }

        if (attacking) {
            if(action == AIRATTACK3 && frame>2 && collideMap(map.getPolygon(), (int) x, (int) (y + 1))) {               // sets action to idle if ground slam attack collides with the ground
                action = IDLE;
                setAttackMove(x + (facing2*5), y+5, 40, 15, dmg);
                dmg = 0;
            }
            if(action == AIRATTACK3) {                                                                                  // descend of ground slam attack does small amount of damage as sword slices through air
                setAttackMove(x + (facing2*5), y+15, 15, 20, 1);
            }
            // various attack moves have their own damage numbers, hitbox locations and sizes
            if(frame > attackFrames[action] && frame < attackFrames[action] + 0.8) {
                if(action == ATTACK1 || action == ATTACK2) {
                    setAttackMove(x + (facing2*10), y, 20, 25, 5);
                }
                if(action == ATTACK3) {
                    setAttackMove(x + (facing2*5), y+5, 40, 15, 15);
                }
                if(action == AIRATTACK1) {
                    setAttackMove(x, y-5, 40, 20, 5);
                }
                if(action == AIRATTACK2) {
                    setAttackMove(x+(facing2*5), y, 30, 25, 5);
                }

            }

            if (frame > frames[action] - 2) {
                if (keyAttack) {                                                                                        // once current attack animation is over, only continues sequence if attack key
                    frame = 0;                                                                                          // is being held
                    if(collideMap(map.getPolygon(), (int) x, (int) (y + 1))){
                        if (action == ATTACK3) {
                            action = ATTACK1;
                        } else {
                            action++;
                        }
                    }
                    else{
                        if (action == AIRATTACK3) {
                            action = IDLE;
                        } else {
                            action++;
                        }
                    }

                }
                else{                                                                                                   // other wise sets action back to idle
                    actionLock = false;
                    action = IDLE;
                }
            }
        }
        if(action == CAST && frame <= spellFrame && frame > spellFrame-0.3) {                                           // checks for spell casting and sets spell move accordingly
            if(currentSpellMove == SPELL1) {
                setSpellMove(0);
                mana -= 50;
                GameState.sfx[GameState.LIGHTNING2].setFramePosition(0);
                GameState.sfx[GameState.LIGHTNING2].start();
            }
            else if(currentSpellMove == SPELL2){
                setSpellMove(1);
                mana-=15;
                GameState.sfx[GameState.LIGHTNING1].setFramePosition(0);
                GameState.sfx[GameState.LIGHTNING1].start();
            }
        }
    }

    public void setAttackMove(double x, double y, int w, int h, int dmg) {                                              // creates new attack given the damage and hitbox
        attackMove = new Attack(x, y, (int)w, (int)h, dmg, 0);
        GameState.sfx[GameState.SWORD].setFramePosition(0);
        GameState.sfx[GameState.SWORD].start();
    }
    // Getters and Setters for combat
    public Attack getAttackMove() { return attackMove; }
    public void setSpellMove(int type) {
        spell = type;
    }
    public int getSpellMove() { return spell; }
    public void setMana(int m) {
        mana = m;
        maxMana = m;
    }
    // Updates frame
    public void updateFrame() {
        if(frame>frames[action]-1) {
            frame = 0;
            actionLock = false;
            if(action == CAST) {
                action = IDLE;
            }
        }
        else if(action == JUMP) {
            double nFrame = Math.abs(vy) / 3;
            frame = 4 - (int)nFrame  - 1;
        }
        else {
            frame += intervals[action];
        }

    }

    public void horizontalMove() {
        x = x + vx;
        if(collideMap(map.getPolygon(), (int)x, (int)y)) {                                                              // if player is colliding after vx is added, further checks have to be done
            if (collideMap(map.getPolygon(), (int) x, (int)(y-Math.abs(vx)))) {                                         // checks if player would still be colliding if player moved slightly up
                x -= vx;                                                                                                // if so, player is against a wall
                while(!collideMap(map.getPolygon(), (int)x, (int)y)) {                                                  // move as close as possible to wall withot colliding
                    x += posNeg(vx);
                }
                x -= posNeg(vx);
                vx = 0;
            }
            else {                                                                                                      // otherwise, player must be walking up a slope
                while(collideMap(map.getPolygon(), (int)x, (int)y)) {                                                   // move player upwards until he isn't colliding and is above the slope
                    y --;
                }
            }
        }
        else if(!collideMap(map.getPolygon(), (int)x, (int)(y+1)) && collideMap(map.getPolygon(), (int)x, (int)(y+Math.abs(vx)))) {// if player isnt colliding with x and y after moving,
            while(!collideMap(map.getPolygon(), (int)x, (int)y+1)) {                                                 //check if player is colliding when increasing y value slightly
                y++;                                                                                                    // if colliding, that means player is walking down slope, increase y until player
            }                                                                                                           // just above slope
        }
    }
    public void verticalMove() {
        y = y + vy;
        if(collideMap(map.getPolygon(), (int)x, (int)y)) {                                                              // if colliding after adding vy
            y -= vy;
            while(!collideMap(map.getPolygon(), (int)x, (int)y)) {                                                      // get as close as possible to ground without colliding
                y += posNeg(vy);
            }
            y -= posNeg(vy);
            vy = 0;
        }

    }

    public int posNeg(double val){                                                                                      // returns -1 for any value lower than or equal 0, and 1 for any value greater than 0
        if(val>=1){ return 1; }
        else if(val<=-1){ return -1; }
        else{ return 0; }
    }

    public boolean collideMap(Polygon[] polygons, int x, int y) {                                                       // checks if any polygon from array contains the x,y position given
        boolean collision = false;
        double[] corners = generateCorners(x, y);

        for(Polygon poly:polygons){
            for(int i=0; i<corners.length/2; i++){
                if(poly.contains(corners[i*2],corners[i*2+1])) { collision = true; }
            }
        }

        return collision;
    }

    public double[] generateCorners(double x, double y) {                                                               // returns an array of x and y values around player to check collision with
        double[] corners = new double[16];
        corners[0] = x + width/2;
        corners[1] = y + height/2;
        corners[2] = x - width/2;
        corners[3] = y + height/2;
        corners[4] = x - width/2;
        corners[5] = y - height/2 + 3;
        corners[6] = x + width/2;
        corners[7] = y - height/2 + 3;
        corners[8] = x + width/2;
        corners[9] = y + height/5;
        corners[10] = x + width/2;
        corners[11] = y - height/5;
        corners[12] = x - width/2;
        corners[13] = y + height/5+3;
        corners[14] = x - width/2;
        corners[15] = y - height/5+3;

        return corners;
    }

    public void draw(Graphics2D g){
        Image pic = images[action][(int)frame];
        if(facing == "right"){
            g.drawImage(pic, (int)(x - sWidth/2), (int)(y - sHeight/2), null);
        }
        else{
            g.drawImage(pic, (int)(x - sWidth/2) + (int)sWidth, (int)(y - sHeight/2), (int)-sWidth, (int)sHeight, null);
        }

        // collision box
        g.setColor(Color.WHITE);

        //double[] corners = generateCorners(x,y);
        //for(int i=0; i<corners.length/2; i++){
        //    g.drawOval((int)corners[i*2],(int)corners[i*2+1],2,2);
        //}
        /*g.drawLine((int)corners[0],(int)corners[1],(int)corners[2],(int)corners[3]);
        g.drawLine((int)corners[2],(int)corners[3],(int)corners[4],(int)corners[5]);
        g.drawLine((int)corners[4],(int)corners[5],(int)corners[6],(int)corners[7]);
        g.drawLine((int)corners[6],(int)corners[7],(int)corners[0],(int)corners[1]);
         */

    }

    public void drawHUD(Graphics2D g, int cx) {
        double hpW = hp/maxHP * 200;
        g.setColor(new Color(50,50,50));
        g.fillRect(cx + 15,380,200,2);
        g.setColor(Color.BLACK);
        g.fillRect(cx + 15,382,200,4);
        g.setColor(Color.RED);
        g.fillRect(cx + 15,380, (int)hpW, 2);
        g.setColor(new Color(200,50,50));
        g.fillRect(cx + 15,382, (int)hpW, 4);
        g.setColor(Color.GRAY);
        g.drawRect(cx+15,380,200,6);

        double manaW = mana/maxMana * maxMana*4;
        g.setColor(new Color(50,50,50));
        g.fillRect(cx + 15,386,200,2);
        g.setColor(Color.BLACK);
        g.fillRect(cx + 15,388,200,4);
        g.setColor(new Color(100,200,255));
        g.fillRect(cx + 15,386, (int)manaW, 2);
        g.setColor(new Color(80,170,255));
        g.fillRect(cx + 15,388, (int)manaW, 4);
        g.setColor(Color.GRAY);
        g.drawRect(cx+15,386,200,6);
    }


    public void keyPressed(int k) { // changes according input flags
        if(k == KeyEvent.VK_W || k == KeyEvent.VK_SPACE) {
            keyUp1 = true;
        }
        if(k == KeyEvent.VK_A) {
            keyLeft = true;
        }
        if(k == KeyEvent.VK_D) {
            keyRight = true;
        }
        if(k == KeyEvent.VK_K) {
            keyAttack = true;
        }
        if(k == KeyEvent.VK_L) {
            keyCast1 = true;
            currentSpellMove = SPELL1;
        }
        if(k == KeyEvent.VK_J) {
            keyCast2 = true;
            currentSpellMove = SPELL2;
        }
    }
    public void keyReleased(int k) {
        if(k == KeyEvent.VK_W  || k == KeyEvent.VK_SPACE) {
            keyUp1 = false;
            resetKeyUp = true;
        }
        if(k == KeyEvent.VK_A) {
            keyLeft = false;
        }
        if(k == KeyEvent.VK_D) {
            keyRight = false;
        }
        if(k == KeyEvent.VK_K) {
            keyAttack = false;
        }
        if(k == KeyEvent.VK_L) {
            keyCast1 = false;
        }
        if(k == KeyEvent.VK_J) {
            keyCast2 = false;
        }
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getFacing() { return facing2; }
    public void setHP(int dmg) {
        hp -= dmg;
    }
    public double getHP() { return hp; }
    public Rectangle getHitRect() {
        Rectangle rect = new Rectangle((int)(x-width/2), (int)(y-height/2), (int)width, (int)height);
        return rect;
    }

}
