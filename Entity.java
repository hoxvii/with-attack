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