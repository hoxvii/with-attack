/**

This class is contains all the components for the game.
This is where the players, enemies, and the map all contained.

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
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.swing.*;
import static utils.Constants.Directions.*;

public class GameFrame extends JFrame {

    private int width, height;
    private Container contentPane;
    private Player player1;
    private Player player2;
    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean run, jump, left, right;

    private double airSpeed = 0, gravity = 0.04, jumpSpeed = -2.25;
    private float fallSpeedAfterCollision;
    private boolean inAir = false;

    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private LevelManager levelManager;
    private EnemyManager enemyManager;

    public static int playerID;
    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.0f;
    public final static int TILES_IN_WIDTH = 24;
    public final static int TILES_IN_HEIGHT = 16;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    private int xLvlOffset;
    private int leftBorder = (int) (0.4 * GAME_WIDTH);
    private int rightBorder = (int) (0.6 * GAME_WIDTH);

    private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * TILES_SIZE;

    private BufferedImage backgroundImg1, backgroundImg2, 
    backgroundImg3, backgroundImg4;


    public GameFrame(int w, int h) {
        width = w;
        height = h;
        right = false;
        left = false;
        backgroundImg1 = LoadSave.GetSpriteAtlas(LoadSave.LAYER1_BG);
        backgroundImg2 = LoadSave.GetSpriteAtlas(LoadSave.LAYER2_BG);
        backgroundImg3 = LoadSave.GetSpriteAtlas(LoadSave.LAYER3_BG);
        backgroundImg4 = LoadSave.GetSpriteAtlas(LoadSave.LAYER4_BG);
        
    }

    public void setUpGUI() {
        contentPane = this.getContentPane();
        this.setTitle("Player# " + playerID);
        contentPane.setPreferredSize(new Dimension(width,height));
        createSprites();
        dc = new DrawingComponent();
        contentPane.add(dc);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        setUpKeyListener();
        this.setVisible(true);
        setUpAnimationTimer();
    }

    private void createSprites() {
        levelManager = new LevelManager(this);
        enemyManager = new EnemyManager(this);
        if (playerID == 1) {
            player2 = new Player(100,342,256,256,"Sun");
            player1 = new Player(100,342,256,256,"Moon");
        } else {
            player2 = new Player(150,342,256,256,"Moon");
            player1 = new Player(150,342,256,256,"Sun");
        }
        player1.loadLevelData(levelManager.getCurrentLevel().getLevelData());
        
    }

    private void checkCloseToBorder() {
        int playerX = (int) player1.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder) {
            xLvlOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLvlOffset += diff - leftBorder;
        }

        if (xLvlOffset > maxLvlOffsetX) {
            xLvlOffset = maxLvlOffsetX;
        } else if (xLvlOffset < 0) {
            xLvlOffset = 0;
        }
    }

    private void setUpAnimationTimer() {
        
        int interval = 10;
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                double playerSpeed = 2, xSpeed = 0;

                if (jump) {
                    jump();
                }

                if (run) {
                    xSpeed = playerSpeed * 1.5;
                }
                
                if (right) {
                    xSpeed = playerSpeed;
                }
                
                if (left) {
                    xSpeed = -playerSpeed;
                    player1.setLeft(true);
                }

                if (inAir) {

                    if (Collision.CanMoveHere(player1.hitbox.x, player1.hitbox.y + airSpeed, player1.hitbox.width, player1.hitbox.height, player1.lvlData)) {
                        player1.hitbox.y += airSpeed;
                        airSpeed += gravity;
                        updateXPos(xSpeed);
                    } else {
                        if (airSpeed > 0) {
                            resetInAir();
                        } else {
                            airSpeed = fallSpeedAfterCollision;
                            updateXPos(xSpeed);
                        }
                    }
                } else {
                    updateXPos(xSpeed);
                }

                    enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player1);
                    enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player2);
                

                dc.repaint();
            }

            private void updateXPos(double xSpeed) {
                if (Collision.CanMoveHere(player1.hitbox.x + xSpeed, player1.hitbox.y, player1.hitbox.width, player1.hitbox.height, player1.lvlData)) {
                    player1.moveH(xSpeed);
                } 
            }

            private void resetInAir() {
                inAir = false;
                airSpeed = 0;
            }

            private void jump() {
                if (inAir) {
                    return;
                }
                inAir = true;
                airSpeed = jumpSpeed;
            }

        };
        animationTimer = new Timer(interval, al);
        animationTimer.start();
    }

    private void setUpKeyListener() {
        KeyListener kl = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }
            @Override 
            public void keyPressed(KeyEvent ke) {
                int keyCode = ke.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_SPACE:
                        jump = true;
                        player1.setJumping(true);
                        break; 
                    case KeyEvent.VK_D:
                        right = true;
                        player1.setDirection(RIGHT);
                        break;
                    case KeyEvent.VK_F:
                        run = true;
                        player1.running(true);
                        break;
                    case KeyEvent.VK_A:
                        left = true;
                        player1.setDirection(LEFT);
                        break;
                    case KeyEvent.VK_P:
                        player1.punching(true);
                        break;
                    case KeyEvent.VK_K:
                        player1.kicking(true);
                        break;
                    case KeyEvent.VK_O:
                        player1.oppPunching(true);
                        break;
                    case KeyEvent.VK_S:
                        player1.crouching(true);
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent ke) {
                int keyCode = ke.getKeyCode();
                
                switch (keyCode) {
                    case KeyEvent.VK_SPACE:
                        jump = false;
                        player1.setJumping(false);
                        break; 
                    case KeyEvent.VK_D:
                        right = false;
                        player1.setMoving(false);
                        break;
                    case KeyEvent.VK_F:
                        run = false;
                        player1.running(false);
                        break;
                    case KeyEvent.VK_A:
                        left = false;
                        player1.setMoving(false);
                        break;
                    case KeyEvent.VK_P:
                        player1.punching(false);
                        break;
                    case KeyEvent.VK_K:
                        player1.kicking(false);
                        break;
                    case KeyEvent.VK_O:
                        player1.oppPunching(false);
                        break;
                    case KeyEvent.VK_S:
                        player1.crouching(false);
                        break;
                }
            }
        };
        contentPane.addKeyListener(kl);
        contentPane.setFocusable(true);
    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", 24049);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            playerID = in.readInt();
            System.out.println("You are player#" + playerID);
            if (playerID == 1) {
                System.out.println("Waiting for Player #2 to connect...");
            }

            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);

            rfsRunnable.waitForStartMsg();


        } catch (IOException ex) {
            System.err.println("IOException from connectToServer()");
        }
    }

    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            checkCloseToBorder();
            
            levelManager.draw(g, xLvlOffset);
            
            g.drawImage(backgroundImg1.getSubimage(xLvlOffset, 0, GAME_WIDTH, GAME_HEIGHT), 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
            g.drawImage(backgroundImg2.getSubimage((int) (xLvlOffset*0.2), 0, GAME_WIDTH, GAME_HEIGHT), 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
            g.drawImage(backgroundImg3.getSubimage((int) (xLvlOffset*0.5), 0, GAME_WIDTH, GAME_HEIGHT), 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
            g.drawImage(backgroundImg4.getSubimage((int) (xLvlOffset*0.8), 0, GAME_WIDTH, GAME_HEIGHT), 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
            levelManager.draw(g, xLvlOffset);
            
            player2.render(g, xLvlOffset);
            player1.render(g, xLvlOffset);
            enemyManager.draw(g, xLvlOffset);
        }

    }

    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;
        public ReadFromServer(DataInputStream in) {
            dataIn = in;
            System.out.println("RFS Runnable created");
        }
        @Override
        public void run() {
            try {
                while(true) {
                    double enemyX = dataIn.readDouble();
                    double enemyY = dataIn.readDouble();
                    boolean moved = dataIn.readBoolean();
                    boolean punched = dataIn.readBoolean();
                    boolean kicked = dataIn.readBoolean();
                    boolean oppPunched = dataIn.readBoolean();
                    boolean ran = dataIn.readBoolean();
                    boolean crouched = dataIn.readBoolean();
                    int xLvlOffset = dataIn.readInt();
                    boolean left = dataIn.readBoolean();
                    boolean right = dataIn.readBoolean();
                    if (player2 != null) {
                        player2.setX(enemyX);
                        player2.setY(enemyY);
                        player2.setMoving(moved);
                        player2.punching(punched);
                        player2.kicking(kicked);
                        player2.oppPunching(oppPunched);
                        player2.running(ran);
                        player2.crouching(crouched);
                        player2.setOffset(xLvlOffset);
                        player2.setLeft(left);
                        player2.setRight(right);
                    }
                }
                
            } catch (IOException ex) {
                System.err.println("IOExxception from RFS run()");
            }
        }

        public void waitForStartMsg() {
            try {
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server: " + startMsg);
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();

            } catch (IOException ex) {
                System.err.println("IOException from waitForStartMsg()");
            }
        }
    }

    private class WriteToServer implements Runnable {

        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out) {
            dataOut = out;
            System.out.println("WTS Runnable created");
        }

        @Override
        public void run() {
            try {
                while(true) {
                    if (player1 != null) {
                        dataOut.writeDouble(player1.getX());
                        dataOut.writeDouble(player1.getY());
                        dataOut.writeBoolean(player1.getIfMoving());
                        dataOut.writeBoolean(player1.getIfPunching());
                        dataOut.writeBoolean(player1.getIfKicking());
                        dataOut.writeBoolean(player1.getIfOppPunching());
                        dataOut.writeBoolean(player1.getIfRunning());
                        dataOut.writeBoolean(player1.getIfCrouching());
                        dataOut.writeInt(player1.getOffset());
                        dataOut.writeBoolean(player1.isLeft());
                        dataOut.writeBoolean(player1.isRight());
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.err.println("InterruptedException from WTS run()");
                    }
                }
            } catch (IOException ex) {
                System.err.println("IOException from WTS run ()");
            }
            
        }
    }
}