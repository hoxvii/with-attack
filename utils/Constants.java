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