//package parkingsystem;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class LoginScreen extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JLabel errorLabel;
//
//    public LoginScreen() {
//        setTitle("Admin Login");
//        setSize(300, 200);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//
//        usernameField = addField("Username:", panel);
//        passwordField = new JPasswordField(15);
//        panel.add(new JLabel("Password:"));
//        panel.add(passwordField);
//
//        JButton loginButton = new JButton("Login");
//        errorLabel = new JLabel();
//        panel.add(loginButton);
//        panel.add(errorLabel);
//        
//        loginButton.addActionListener(new LoginButtonListener());
//
//        add(panel);
//    }
//
//    private JTextField addField(String labelText, JPanel panel) {
//        JLabel label = new JLabel(labelText);
//        panel.add(label);
//        JTextField textField = new JTextField(15);
//        panel.add(textField);
//        return textField;
//    }
//
//    private class LoginButtonListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            String username = usernameField.getText();
//            String password = new String(passwordField.getPassword());
//
//            if (username.isEmpty() || password.isEmpty()) {
//                errorLabel.setText("Please enter both username and password.");
//                return;
//            }
//
//            if (new DBOperation().adminLogin(username, password)) {
//                errorLabel.setText("Login Successful");
//                new HomeScreen().setVisible(true);
//                dispose();
//            } else {
//                errorLabel.setText("Invalid Login Details");
//            }
//        }
//    }
//}
