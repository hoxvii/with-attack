import java.awt.Graphics;
import static utils.Constants.EnemyConstants.*;

public class Goblin extends Enemy {

    public Goblin(double x, double y) {
        super(x, y, GOBLIN_WIDTH, GOBLIN_HEIGHT, GOBLIN);
        initHitbox(x, y, 38, 91);

    }

    public void render(Graphics g, int frameOffset) {
        drawHitbox(g, frameOffset);
    }

    public void update(int[][] lvlData, PlayerSprite player) {
        updateMove(lvlData, player);
        updateAnimationTick();
    }

    public void updateMove(int[][] lvlData, PlayerSprite player) {
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }

        if (inAir) {
            updateInAir(lvlData);
            move(lvlData); 
        } else {
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if(canSeePlayer(lvlData, player)) {
                        turnTowardsPlayer(player);
                    } if (isPlayerCloseForAttack(player)) {
                        newState(ATTACK);
                    }
                    move(lvlData);
                    break;
            }
            
            if (!Collision.IsFloor(hitbox, 0, lvlData)) {
                inAir = true;
                fallSpeed = 0;
            }
        }
    }
}