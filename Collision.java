/**

This class contains all static methods necessary for checking of collision.
It is used by other classes to check whether a player or an enemy can pass through a tile.

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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class Collision {

    // This static method returns whether an entity can move to a certain space.
    public static boolean CanMoveHere(double x, double y, double width, double height, int[][] lvlData) {
        if(!IsSolid(x, y, lvlData))
            if(!IsSolid(x+width, y, lvlData))
                if(!IsSolid(x, y+height, lvlData))
                    if(!IsSolid(x+width, y+height, lvlData))
                        return true;

        return false;
    }

    // This static method returns whether a tile is passable or not.
    private static boolean IsSolid(double x, double y, int[][] lvlData) {

        int maxWidth = lvlData[0].length * GameFrame.TILES_SIZE;

        if (x < 0 || x >= maxWidth) {
            return true;
        }
        if (y < 0 || y >= GameFrame.GAME_HEIGHT){
            return true;
        }

        int tileX = (int)(x / GameFrame.TILES_SIZE);
        int tileY = (int)(y / GameFrame.TILES_SIZE);

        return IsTileSolid(x, y, tileX, tileY, lvlData);

    }
    
    public static boolean IsTileSolid(double x, double y, int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile];

        if(value >= 15 || value < 0 ) {
            return true;
        }

        if (!(lvlData[(int) (y)/GameFrame.TILES_SIZE][(int) (x)/GameFrame.TILES_SIZE] <= 0)) {
            return true;
        }

        return false;
    }

    // This static method returns a position when a character is next to a wall.
    public static double GetEntityXPosNextToWall(Rectangle2D.Double hitbox, double xSpeed) {

        double currentTile = (hitbox.x / GameFrame.TILES_SIZE);
        if (xSpeed > 0) {
            double tileXPos = currentTile * GameFrame.TILES_SIZE;
            double xOffset = GameFrame.TILES_SIZE - hitbox.width;
            return tileXPos + xOffset;
        } else {
            return currentTile * GameFrame.TILES_SIZE;
        }

    }

    // This static method returns a position when a character is next to the floor.
    public static double GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Double hitbox, double airSpeed) {
        int currentTile = (int) (hitbox.x / GameFrame.TILES_SIZE);
        if (airSpeed > 0) {
            int tileYPos = currentTile * GameFrame.TILES_SIZE;
            int yOffset = (int) (GameFrame.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else {
            return currentTile * GameFrame.TILES_SIZE;
        }
    }

    // This static method identifies whether an entity is set on the ground.
    public static boolean IsEntityOnFLoor(Rectangle2D.Double hitbox, int[][] lvlData) {
        if(!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) {
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData)) {
                return false;
            }
        }
        return true;
    }

    // This static method identifies whether a space is a ground.
    public static boolean IsFloor(Rectangle.Double hitbox, double xOffset, int[][] lvlData) {
        double x1 = hitbox.x + xOffset;
        double x2 = x1 + hitbox.width;

        return IsSolid(x1, hitbox.y + hitbox.height + 1, lvlData) && IsSolid(x2 - 1, hitbox.y + hitbox.height + 1, lvlData);

    }

    // This static method helps an enemy see whether a player is nearby.
    public static boolean IsSightClear(double x, double y, int[][] lvlData, Rectangle2D.Double hitboxOne, Rectangle2D.Double hitboxTwo, int yTile) {
        int firstXTile = (int) (hitboxOne.x / GameFrame.TILES_SIZE);
        int secondXTile = (int) (hitboxTwo.x / GameFrame.TILES_SIZE);

        if (firstXTile > secondXTile) {
            for (int i = 0; i < firstXTile - secondXTile; i++) {
                if (IsTileSolid(x, y, secondXTile + i, yTile, lvlData)) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < secondXTile - firstXTile; i++) {
                if (IsTileSolid(x, y, firstXTile + i, yTile, lvlData)) {
                    return false;
                }
            }
        }
        return true;
    }
}