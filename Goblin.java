/**

This class is a non-playable character who will also serve as the enemy.
Thus, it extends the enemy abstract class to contain all nececssary methods 
to an enemy.

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

    public void update(int[][] lvlData, Player player) {
        updateMove(lvlData, player);
        updateAnimationTick();
    }

    public void updateMove(int[][] lvlData, Player player) {
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