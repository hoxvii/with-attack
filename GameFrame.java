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
    private PlayerSprite me;
    private PlayerSprite enemy;
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
        levelManager.update();
    }

    private void createSprites() {
        levelManager = new LevelManager(this);
        enemyManager = new EnemyManager(this);
        if (playerID == 1) {
            enemy = new PlayerSprite(100,342,256,256,"Sun");
            me = new PlayerSprite(100,342,256,256,"Moon");
        } else {
            enemy = new PlayerSprite(150,342,256,256,"Moon");
            me = new PlayerSprite(150,342,256,256,"Sun");
        }
        me.loadLevelData(levelManager.getCurrentLevel().getLevelData());
        
    }

    private void checkCloseToBorder() {
        int playerX = (int) me.getHitbox().x;
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
                    me.setLeft(true);
                }

                if (inAir) {

                    if (Collision.CanMoveHere(me.hitbox.x, me.hitbox.y + airSpeed, me.hitbox.width, me.hitbox.height, me.lvlData)) {
                        me.hitbox.y += airSpeed;
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

                    enemyManager.update(levelManager.getCurrentLevel().getLevelData(), me);
                    enemyManager.update(levelManager.getCurrentLevel().getLevelData(), enemy);
                

                dc.repaint();
            }

            private void updateXPos(double xSpeed) {
                if (Collision.CanMoveHere(me.hitbox.x + xSpeed, me.hitbox.y, me.hitbox.width, me.hitbox.height, me.lvlData)) {
                    me.moveH(xSpeed);
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
                        me.setJumping(true);
                        break; 
                    case KeyEvent.VK_D:
                        right = true;
                        me.setDirection(RIGHT);
                        break;
                    case KeyEvent.VK_F:
                        run = true;
                        me.running(true);
                        break;
                    case KeyEvent.VK_A:
                        left = true;
                        me.setDirection(LEFT);
                        break;
                    case KeyEvent.VK_P:
                        me.punching(true);
                        break;
                    case KeyEvent.VK_K:
                        me.kicking(true);
                        break;
                    case KeyEvent.VK_O:
                        me.oppPunching(true);
                        break;
                    case KeyEvent.VK_S:
                        me.crouching(true);
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent ke) {
                int keyCode = ke.getKeyCode();
                
                switch (keyCode) {
                    case KeyEvent.VK_SPACE:
                        jump = false;
                        me.setJumping(false);
                        break; 
                    case KeyEvent.VK_D:
                        right = false;
                        me.setMoving(false);
                        break;
                    case KeyEvent.VK_F:
                        run = false;
                        me.running(false);
                        break;
                    case KeyEvent.VK_A:
                        left = false;
                        me.setMoving(false);
                        break;
                    case KeyEvent.VK_P:
                        me.punching(false);
                        break;
                    case KeyEvent.VK_K:
                        me.kicking(false);
                        break;
                    case KeyEvent.VK_O:
                        me.oppPunching(false);
                        break;
                    case KeyEvent.VK_S:
                        me.crouching(false);
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
            
            enemy.render(g, xLvlOffset);
            me.render(g, xLvlOffset);
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
                    if (enemy != null) {
                        enemy.setX(enemyX);
                        enemy.setY(enemyY);
                        enemy.setMoving(moved);
                        enemy.punching(punched);
                        enemy.kicking(kicked);
                        enemy.oppPunching(oppPunched);
                        enemy.running(ran);
                        enemy.crouching(crouched);
                        enemy.setOffset(xLvlOffset);
                        enemy.setLeft(left);
                        enemy.setRight(right);
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
                    if (me != null) {
                        dataOut.writeDouble(me.getX());
                        dataOut.writeDouble(me.getY());
                        dataOut.writeBoolean(me.getIfMoving());
                        dataOut.writeBoolean(me.getIfPunching());
                        dataOut.writeBoolean(me.getIfKicking());
                        dataOut.writeBoolean(me.getIfOppPunching());
                        dataOut.writeBoolean(me.getIfRunning());
                        dataOut.writeBoolean(me.getIfCrouching());
                        dataOut.writeInt(me.getOffset());
                        dataOut.writeBoolean(me.isLeft());
                        dataOut.writeBoolean(me.isRight());
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