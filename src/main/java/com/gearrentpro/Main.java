package main.java.com.gearrentpro;

import main.java.com.gearrentpro.controller.LoginController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginController().setVisible(true));
    }
}