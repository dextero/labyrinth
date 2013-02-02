import pl.labyrinth.Game;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/25/12
 * Time: 6:44 PM
 */
public class Main {
    public static void main(String[] args) {
        System.out.printf("java.library.path = %s\n", System.getProperty("java.library.path"));
        System.out.printf("jna.library.path = %s\n", System.getProperty("jna.library.path"));
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Game game = new Game();
        game.run();
    }
}
