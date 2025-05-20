/**

This class contains all static methods and objects necessary for
getting different constants in the game. It contains constants for
the players and the enemies.

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


package utils;

public class Constants {

    public static int GetSpriteAmount;

    public static class EnemyConstants {
        public static final int GOBLIN = 0;

        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK = 2;
        public static final int HIT = 3;
        public static final int DEAD = 4;

        public static final int GOBLIN_WIDTH = 237;
        public static final int GOBLIN_HEIGHT = 121;

        public static final int GOBLIN_DRAWOFFSET_X = 95;
        public static final int GOBLIN_DRAWOFFSET_Y = 28;

        public static int GetSpriteAmount(int enemyType, int enemyState) {
            switch(enemyType) {
                case GOBLIN:
                    switch(enemyState) {
                        case IDLE:
                        case HIT:
                        case DEAD:
                            return 4;
                        case RUNNING:
                        case ATTACK:
                            return 8;
                    }
            }
            return 0;
        }

    }

    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class PlayerConstants {
        public static final int IDLE = 0;
        public static final int WALKING = 1;
        public static final int RUNNING = 2;
        public static final int JUMPING = 3;
        public static final int CROUCHING = 4;
        public static final int PUNCHING1 = 5;
        public static final int PUNCHING2 = 6;
        public static final int KICKING = 7;

        public static int GetSpriteAmount(int player_action) {

            switch(player_action) {
                case IDLE:
                    return 8;
                case WALKING:
                case RUNNING:
                    return 12;
                case JUMPING:
                    return 11;
                case CROUCHING:
                    return 6;
                case PUNCHING1:
                    return 5;
                case PUNCHING2:
                    return 3;
                case KICKING:
                    return 5;
                default:
                    return 1;
                    
            }
        }
    }
}