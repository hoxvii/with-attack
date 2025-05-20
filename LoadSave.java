/**

This class contains all the static variables and methods necessary
for importing images ang getting the data from the level map.
It translates the rgb codes to set different tiles and entities.

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
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import static utils.Constants.EnemyConstants.*;

public class LoadSave {

    public static final String PLAYER1_ATLAS = "Images/Player1_Sprites.png";
    public static final String PLAYER2_ATLAS = "Images/Player2_Sprites.png";
    public static final String LEVEL_ATLAS = "Images/Outside_Sprites.png";
    public static final String LEVEL_DATA = "Images/LevelData.png";
    public static final String LAYER1_BG = "Images/Layer1_bg.png";
    public static final String LAYER2_BG = "Images/Layer2_bg.png";
    public static final String LAYER3_BG = "Images/Layer3_bg.png";
    public static final String LAYER4_BG = "Images/Layer4_bg.png";
    public static final String GOBLIN_SPRITE = "Images/Goblin_Sprites.png";
    public static final String STATUS_BAR = "Images/Health_Bar.png";

    public static BufferedImage GetSpriteAtlas(String filename) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + filename);
        try {
            img = ImageIO.read(is);
        } catch (IOException ex) {
            System.err.println("IOException fLoadSave");
        }
        return img;
    }

    public static ArrayList<Goblin> GetGoblins() {
        BufferedImage img = GetSpriteAtlas(LEVEL_DATA);
        ArrayList<Goblin> list = new ArrayList<>();
        
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getGreen();
                if (value == GOBLIN) {
                    list.add(new Goblin(i * GameFrame.TILES_SIZE,j * GameFrame.TILES_SIZE));
                }
            }
        }
        return list;
    }

    public static int[][] GetLevelData() {
        BufferedImage img = GetSpriteAtlas(LEVEL_DATA);
        int[][] lvlData = new int[img.getHeight()][img.getWidth()];

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getRed();
                if (value >= 15) {
                    value = 0;
                }
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }
    
}