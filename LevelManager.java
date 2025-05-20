
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

    public void update() {
        
    }

    public Level getCurrentLevel() {
        return levelOne;
    }

    
}