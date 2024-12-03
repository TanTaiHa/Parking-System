package parkingsystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private List<Vehicle> parkedVehicles = new ArrayList<>();
    private JLabel[] slotLabels = new JLabel[36];

    public HomeScreen() {
        setTitle("Parking Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // CardLayout and card panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Side menu
        JPanel sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));

        JButton btnHome = new JButton("Home");
        JButton btnAddVehicle = new JButton("Add Vehicle");
        JButton btnManageVehicle = new JButton("Manage Vehicle");
        JButton btnHistory = new JButton("History");

        btnHome.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
        btnAddVehicle.addActionListener(e -> cardLayout.show(cardPanel, "AddVehicle"));
        btnManageVehicle.addActionListener(e -> cardLayout.show(cardPanel, "ManageVehicle"));
        btnHistory.addActionListener(e -> cardLayout.show(cardPanel, "History"));

        sideMenuPanel.add(btnHome);
        sideMenuPanel.add(btnAddVehicle);
        sideMenuPanel.add(btnManageVehicle);
        sideMenuPanel.add(btnHistory);

        // Panels
        JPanel homePanel = createHomePanel();
        JPanel addVehiclePanel = createAddVehiclePanel();
        JPanel manageVehiclePanel = createManageVehiclePanel();
        JPanel historyPanel = createHistoryPanel();

        cardPanel.add(homePanel, "Home");
        cardPanel.add(addVehiclePanel, "AddVehicle");
        cardPanel.add(manageVehiclePanel, "ManageVehicle");
        cardPanel.add(historyPanel, "History");

        add(sideMenuPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createHomePanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.BOTH;

    // Add gates
    JLabel gate1 = createGateLabel("Gate 1", Color.RED);
    gbc.gridx = 3; // Centered horizontally at the top
    gbc.gridy = 0; // Top row
    panel.add(gate1, gbc);

    JLabel gate2 = createGateLabel("Gate 2", Color.BLUE);
    gbc.gridx = 7; // Positioned to the far right of the slot grid
    gbc.gridy = 3; // Centered vertically
    panel.add(gate2, gbc);

    JLabel gate3 = createGateLabel("Gate 3", Color.GREEN);
    gbc.gridx = 0; // Positioned to the far left of the slot grid
    gbc.gridy = 3; // Centered vertically
    panel.add(gate3, gbc);

    // Add parking slots
    int slotIndex = 0;
    for (int row = 1; row <= 6; row++) {
        for (int col = 1; col <= 6; col++) {
            JLabel slot = new JLabel("Slot " + (slotIndex + 1), SwingConstants.CENTER);
            slot.setOpaque(true);
            slot.setBackground(Color.LIGHT_GRAY);
            slot.setPreferredSize(new Dimension(60, 60));
            slotLabels[slotIndex] = slot;

            gbc.gridx = col; // Slot grid starts from column 1
            gbc.gridy = row; // Rows start from row 1
            panel.add(slot, gbc);

            slotIndex++;
        }
    }

    return panel;
}


    private JLabel createGateLabel(String text, Color color) {
        JLabel gateLabel = new JLabel(text, SwingConstants.CENTER);
        gateLabel.setOpaque(true);
        gateLabel.setBackground(color);
        gateLabel.setPreferredSize(new Dimension(100, 50));
        return gateLabel;
    }

    private JPanel createAddVehiclePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField vehicleNumberField = new JTextField();
        JTextField mobileField = new JTextField();
        JComboBox<String> gateComboBox = new JComboBox<>(new String[]{"Gate 1", "Gate 2", "Gate 3"});
        JButton saveButton = new JButton("SAVE VEHICLE");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Vehicle Number:"));
        panel.add(vehicleNumberField);
        panel.add(new JLabel("Mobile No:"));
        panel.add(mobileField);
        panel.add(new JLabel("Choose Gate:"));
        panel.add(gateComboBox);
        panel.add(saveButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String vehicleNumber = vehicleNumberField.getText();
            String mobile = mobileField.getText();
            int gateIndex = gateComboBox.getSelectedIndex();

            // Allocate nearest slot
            SlotAllocator allocator = new SlotAllocator(new ArrayList<>());
            int slotIndex = allocator.getNearestAvailableSlot(gateIndex == 0 ? 2 : gateIndex == 1 ? 17 : 14);

            if (slotIndex != -1) {
                parkedVehicles.add(new Vehicle(name, vehicleNumber, mobile, gateIndex));
                slotLabels[slotIndex].setBackground(Color.MAGENTA);
                JOptionPane.showMessageDialog(this, "Vehicle added successfully to Slot " + (slotIndex + 1) + "!");
            } else {
                JOptionPane.showMessageDialog(this, "No available slots near the selected gate.");
            }

            nameField.setText("");
            vehicleNumberField.setText("");
            mobileField.setText("");
        });

        return panel;
    }

    private JPanel createManageVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Manage Vehicle Panel (to be implemented)"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("History Panel (to be implemented)"), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomeScreen::new);
    }
}
