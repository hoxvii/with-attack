import java.awt.Rectangle;
import static utils.Constants.Directions.*;
import static utils.Constants.EnemyConstants.*;

public abstract class Enemy extends Entity {

    protected int aniIndex, enemyState, enemyType, aniTick, aniSpeed = 7;
    protected boolean firstUpdate = true, inAir = true;
    protected double fallSpeed, gravity = 0.09;
    protected double walkSpeed = 2;
    protected double walkDir = LEFT;
    protected int tileY;
    protected double attackDistance = GameFrame.TILES_SIZE;
    
    public Enemy(double x, double y, double width, double height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitbox(x, y, width, height);

    }

    protected void firstUpdateCheck(int[][] lvlData) {
        if (Collision.IsFloor(hitbox, 0, lvlData)) {
                inAir = false;
            }
            firstUpdate = false;
    }

    protected void updateInAir(int[][] lvlData) {
        if (Collision.CanMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += fallSpeed;
                fallSpeed += gravity;
        } else {
            inAir = false;
            fallSpeed = 0;
            hitbox.y =  (hitbox.y / GameFrame.TILES_SIZE) * GameFrame.TILES_SIZE;
            tileY = (int) (hitbox.y / GameFrame.TILES_SIZE);
        }
    }

    protected void turnTowardsPlayer(PlayerSprite player) {
        if (player.hitbox.x > hitbox.x) {
            walkDir = RIGHT;
        } else {
            walkDir = LEFT;
        }
    }

    protected boolean canSeePlayer(int[][] lvlData, PlayerSprite player) {
        int playerTileY = (int) (player.getHitbox().y / GameFrame.TILES_SIZE);
        if (playerTileY == tileY) {
            if(isPlayerInRange(player)) {
                if(Collision.IsSightClear(player.getHitbox().x, player.getHitbox().y, lvlData, hitbox, player.hitbox, tileY)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isPlayerInRange(PlayerSprite player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance * 5;
    }

    protected boolean isPlayerCloseForAttack(PlayerSprite player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }

    protected void newState (int enemyState) {
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    protected void move(int[][] lvlData) {
        double xSpeed = 0;
        if (walkDir == LEFT) {
            xSpeed = -walkSpeed;
        } else {
            xSpeed = walkSpeed;
        }
        if (Collision.CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            Rectangle.Double futureHitbox = new Rectangle.Double(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height);
            if (Collision.IsFloor(futureHitbox, 0, lvlData)) {
                hitbox.x += xSpeed;
                return;
            }
        }
        changeWalkDir();
    }

    protected void updateAnimationTick() {
        aniTick ++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;
                if (enemyState == ATTACK) {
                    enemyState = IDLE;
                }
            }
        }
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }

    protected void changeWalkDir() {
        if (walkDir ==  LEFT) {
            walkDir = RIGHT;
        } else {
            walkDir = LEFT;
        }
    }
}