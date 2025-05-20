/**

This class manages the game map. It imports the sprites for the 
tiles from the spritesheet and sets them in the frame.

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

public class LevelManager {
    private GameFrame gf;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(GameFrame gf) {
        this.gf = gf;
        importOutsideSprites();
        levelOne = new Level(LoadSave.GetLevelData());
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[15];
        for (int i = 0; i < 15; i++) {
            levelSprite[i] = img.getSubimage(i * 64, 0, 64, 64);
        }
    }

    public void draw(Graphics g, int lvlOffset) {
        for (int j = 0; j < GameFrame.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], GameFrame.TILES_SIZE * i - lvlOffset, GameFrame.TILES_SIZE * j, GameFrame.TILES_SIZE, GameFrame.TILES_SIZE, null);
            }
        }
    }

    public Level getCurrentLevel() {
        return levelOne;
    }

    
}