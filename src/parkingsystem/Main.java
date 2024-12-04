package parkingsystem;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Delete config.json at startup to reset the application each time
            File configFile = new File("config.json");
            if (configFile.exists()) {
                configFile.delete();
            }
            
            JFrame splashScreen = new JFrame();
            splashScreen.setUndecorated(true);
            splashScreen.setSize(500, 300);
            splashScreen.setLocationRelativeTo(null);
            splashScreen.setVisible(true);

            Timer timer = new Timer(3000, e -> {
                splashScreen.dispose();
                new SetupWindow().setVisible(true); // Always show SetupWindow first
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
