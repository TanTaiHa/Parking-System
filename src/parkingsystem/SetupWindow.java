package parkingsystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import net.sf.json.JSONObject;

public class SetupWindow extends JFrame {
    private JTextField dbNameField, adminUsernameField, adminPasswordField;
    private JLabel errorLabel;

    public SetupWindow() {
        setTitle("Install Vehicle Parking System");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Initialize fields
        dbNameField = addField("Database Name:", "vehicle_parking", panel);
        adminUsernameField = addField("Admin Username:", "", panel);
        adminPasswordField = addField("Admin Password:", "", panel);

        JButton saveButton = new JButton("Save Config");
        panel.add(saveButton);

        errorLabel = new JLabel();
        panel.add(errorLabel);

        saveButton.addActionListener(new SaveButtonListener());

        add(panel);
    }

    private JTextField addField(String labelText, String defaultValue, JPanel panel) {
        JLabel label = new JLabel(labelText);
        panel.add(label);
        JTextField textField = new JTextField(defaultValue, 15);
        panel.add(textField);
        return textField;
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Check that all fields are filled
            if (dbNameField.getText().isEmpty() || adminUsernameField.getText().isEmpty() || adminPasswordField.getText().isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            try {
                // Create configuration JSON
                JSONObject config = new JSONObject();
                config.put("database", dbNameField.getText());

                // Write configuration to a file
                try (FileWriter file = new FileWriter("config.json")) {
                    file.write(config.toString());
                }

                // Reset the database with the provided configuration
                new DBOperation().resetDatabase(
                    adminUsernameField.getText(),
                    adminPasswordField.getText(),
                    36 // Fixed number of parking slots
                );

                // Open the LoginScreen
                new LoginScreen().setVisible(true);
                dispose();

            } catch (IOException ioException) {
                errorLabel.setText("Error saving configuration file.");
            } catch (Exception ex) {
                errorLabel.setText("Error setting up database.");
            }
        }
    }
}
