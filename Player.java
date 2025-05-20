/**

This class contains all necessary methods needed by the player.
It extends the entity class to obtain things necessar for combat and
gameplay like hitboxes and attackboxes.

@author Joshua Patrick I. Bandola (240499) Carl A. Basco (240558)
@version 20 May 2024

I have not discussed the Java language code in my program
with anyone other than my instructor or the teaching assistants
assigned to this course.

I have not used Java language code obtained from another student,
or any other unauthorized source, either modified or unmodified.
If any Java language code or documentation used in my program
was obtained from another source, such as a textbook or website,
that has been clearly noted with a proper citation in the comments
of my program.

**/

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import static utils.Constants.PlayerConstants.*;

public class Player extends Entity{

    private int playerNum;
    private BufferedImage image, bgImage;
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed;
    private int playerAction;
    private int playerDir;
    private boolean left, right;
    private boolean facingLeft = true;
    private boolean isMoving, isRunning, isJumping, isPunching, isKicking, isOppPunching, isCrouching;
    protected int[][] lvlData;
    private int xDrawOffset, yDrawOffset, xFrameOffset;

    private BufferedImage statusBarImg;

    private int statusBarWidth = 192;
    private int statusBarHeight = 58;
    private int statusBarX = 15;
    private int statusBarY = 0;

    private int healthBarWidth = 135;
    private int healthBarHeight = 4;
    private int healthBarXStart = 30;
    private int healthBarYStart = 25;

    private int maxHealth = 100;
    private int currentHealth = 40;
    private int healthWidth = healthBarWidth;

    private Rectangle2D.Double attackBox;

    public Player(double x, double y, int width, int height, String character) {
        super(x,y,width,height);
        loadAnimations(character);
        aniSpeed = 5;
        playerAction = IDLE;
        playerDir = -1;
        isMoving = false;
        isPunching = false;
        isKicking = false;
        xDrawOffset = 92;
        yDrawOffset = 100;
        initHitbox(x,y,48,136);
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Double(x,y,20,20);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(playerAction)) {
                aniIndex = 0;
            }
        }
    }

    private void loadAnimations(String character) {

        if (character.equals("Moon")) {
            image = LoadSave.GetSpriteAtlas(LoadSave.PLAYER1_ATLAS);
        } else {
            image = LoadSave.GetSpriteAtlas(LoadSave.PLAYER2_ATLAS);
        }

        animations = new BufferedImage[8][12];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
            animations[j][i] = image.getSubimage(i*256,j*256,256,256);
            }
        }

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

    }

    private void updateHealthbar() {
        healthWidth = (int) ((currentHealth / (double) maxHealth) * healthBarWidth);
    }

    private void changeHealth(int value) {
        currentHealth += value;

        if (currentHealth <= 0) {
            currentHealth = 0;
        } else if (currentHealth >= maxHealth) {
            currentHealth = maxHealth;
        }
    }

    private void updateAttackBox() {
        if (isPunching || isKicking) {
            attackBox.y = hitbox.y + hitbox.height / 3;
            if (facingLeft) {
                attackBox.x = hitbox.x - attackBox.width - 20;
            } else {
                attackBox.x = hitbox.x + hitbox.width + 20;
            }
        } else {
            attackBox.x = -100;
            attackBox.y = -100;
        }
    }

    public void loadLevelData(int[][] lvlData) {
        this.lvlData = lvlData;
    }

    private void setAnimation() {
        int startAni = playerAction;
        if (isMoving) {
            playerAction = WALKING;
        } else {
            playerAction = IDLE;
        }
        if (isRunning) {
            playerAction = RUNNING;
        }
        if (isJumping) {
            playerAction = JUMPING;
        }
        if (isPunching) {
            playerAction = PUNCHING1;
        }
        if (isOppPunching) {
            playerAction = PUNCHING2;
        }
        if (isKicking) {
            playerAction = KICKING;
        }
        if (isCrouching) {
            playerAction = CROUCHING;
        }
        if (startAni != playerAction) {
            resetAniTick();
        }
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    public void render(Graphics g, int lvlOffset) {
        updateAnimationTick();
        updateHealthbar();
        updateAttackBox();
        setAnimation();

        g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset, (int) (hitbox.y - yDrawOffset), (int) width, (int) height, null);
        drawHitbox(g, lvlOffset);
        drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    public void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    private void drawAttackBox(Graphics g, int lvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - lvlOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
    }

    public void setLeft(boolean left) {
        this.left = left;
        if (left) {
            facingLeft = true;
            right = false;
        }
    }

    public boolean isLeft() {
        return left;
    }

    public void setRight(boolean right) {
        this.right = right;
        if (right) {
            facingLeft = false;
            left = false; 
        }
    }

    public boolean isRight() {
        return right;
    }

    public void moveH(double n) {
        x += n;
        updateHitbox();
    }

    public void moveV(double n) {
        y += n;
        updateHitbox();
    }

    public void setX(double n) {
        x = n;
        updateHitbox();
    }

    public void setY(double n) {
        y = n;
        updateHitbox();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setOffset(int offset) {
        xFrameOffset = offset;
    }

    public int getOffset() {
        return xFrameOffset;
    }

    public int getXHitBoxCheckingForBorder() {
        return (int) getHitbox().x;
    }

    public int getDirection() {
        return playerDir;
    }
    
    public void setDirection(int direction) {
        this.playerDir = direction;
        isMoving = true;

        if (direction < 0) {
            facingLeft = true;
        } else if (direction > 0) {
            facingLeft = false;
        }
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean getIfMoving() {
        return isMoving;
    }

    public void running(boolean running) {
        isRunning = running;
    }

    public boolean getIfRunning() {
        return isRunning;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public boolean getIfJumping() {
        return isJumping;
    }

    public void punching(boolean punching) {
        isPunching = punching;
    }

    public boolean getIfPunching() {
        return isPunching;
    }

    public void oppPunching(boolean oppPunching) {
        isOppPunching = oppPunching;
    }

    public boolean getIfOppPunching() {
        return isOppPunching;
    }

    public void kicking(boolean kicking) {
        isKicking = kicking;
    }

    public boolean getIfKicking() {
        return isKicking;
    }

    public void crouching(boolean crouching) {
        isCrouching = crouching;
    }

    public boolean getIfCrouching() {
        return isCrouching;
    }

}