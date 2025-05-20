/**

This class is an abstract class which will be used by the playable
non-playable characters. It has methods for setting the hit- and attack
boxes necessary for combat.

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

public abstract class Entity {

    protected double x, y, width, height;
    protected Rectangle2D.Double hitbox;

    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void drawHitbox(Graphics g, int xLevelOffset) {
        g.setColor(Color.PINK);
        g.drawRect((int) hitbox.x - xLevelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
    }

    protected void initHitbox(double x, double y, double width, double height) {
        hitbox = new Rectangle2D.Double(x, y, width, height);
    }

    public void updateHitbox() {
        hitbox.x = (int) x;
        hitbox.y = (int) y;
    }

    public Rectangle2D.Double getHitbox() {
        return hitbox;
    }
}