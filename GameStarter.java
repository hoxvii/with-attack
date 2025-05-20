/**

This is the main class used to instantiate the game frame.

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

public class GameStarter {
    public static void main(String[] args) {
        GameFrame gf = new GameFrame(GameFrame.GAME_WIDTH,GameFrame.GAME_HEIGHT);
        gf.connectToServer();
        gf.setUpGUI();
    }
}

