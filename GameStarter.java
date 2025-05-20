public class GameStarter {
    public static void main(String[] args) {
        GameFrame gf = new GameFrame(GameFrame.GAME_WIDTH,GameFrame.GAME_HEIGHT);
        gf.connectToServer();
        gf.setUpGUI();
    }
}

