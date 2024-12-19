package parkingsystem;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            File configFile = new File("config.json");
            
            JFrame splashScreen = new JFrame();
            splashScreen.setUndecorated(true);
            splashScreen.setSize(500, 300);
            splashScreen.setLocationRelativeTo(null);
            splashScreen.setVisible(true);

            Timer timer = new Timer(3000, e -> {
                splashScreen.dispose();

                // Kiểm tra config.json
                if (configFile.exists()) {
                    // Nếu file config.json tồn tại, chuyển đến HomeScreen
                    new HomeScreen().setVisible(true);
                } else {
                    // Nếu file config.json không tồn tại, chuyển đến SetupWindow
                    new SetupWindow().setVisible(true);
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
