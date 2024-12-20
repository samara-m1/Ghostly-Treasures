import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) throws Exception {
        //creation and setup of window and frame
        int windowHeight = 400;
        int windowWidth = 900;
        JFrame frame = new JFrame("ghostly treasures!");
        frame.setSize(windowWidth, windowHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        HauntedHouse hauntedHouse = new HauntedHouse(); //creating new haunted house game
        frame.add(hauntedHouse); //add haunted house to frame
        frame.pack(); //layout management
        hauntedHouse.requestFocus(); 
        frame.setVisible(true); //set frame visible

    }
}
