/**

This class is for managing the enemies especially their position.
They are set according to the level data that will be read according to its rgb.

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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static utils.Constants.EnemyConstants.*;

public class EnemyManager {

    private GameFrame gf;
    private BufferedImage[][] goblinArray;
    private ArrayList<Goblin> goblins = new ArrayList<>();

    public EnemyManager(GameFrame gf) {
        this.gf = gf;
        loadEnemyImgs();
        addEnemies();
    }

    public void update(int[][] lvlData, Player player) {
        for(Goblin g  : goblins) {
            g.update(lvlData, player);
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawGoblins(g, xLvlOffset);
    }

    private void loadEnemyImgs() {
        goblinArray = new BufferedImage[5][8];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.GOBLIN_SPRITE);
        for(int j = 0; j < goblinArray.length; j++) {
            for(int i = 0; i < goblinArray[j].length; i++) {
                goblinArray[j][i] = temp.getSubimage(i * GOBLIN_WIDTH, j * GOBLIN_HEIGHT, GOBLIN_WIDTH, GOBLIN_HEIGHT);
            }
        }
    }

    private void drawGoblins(Graphics g, int xLvlOffset) {
        for(Goblin gb  : goblins) {
            g.drawImage(goblinArray[gb.getEnemyState()][gb.getAniIndex()], (int) (gb.getHitbox().x - GOBLIN_DRAWOFFSET_X) - xLvlOffset, (int) gb.getHitbox().y - GOBLIN_DRAWOFFSET_Y, GOBLIN_WIDTH, GOBLIN_HEIGHT, null);
            gb.render(g, xLvlOffset);
        }
    }

    private void addEnemies() {
        goblins = LoadSave.GetGoblins();
    }
}