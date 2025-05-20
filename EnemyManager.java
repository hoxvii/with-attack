
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

    public void update(int[][] lvlData, PlayerSprite player) {
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