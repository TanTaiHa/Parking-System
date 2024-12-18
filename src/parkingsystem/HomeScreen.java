package parkingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeScreen extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private List<Vehicle> parkedVehicles = new ArrayList<>();
    private JLabel[] slotLabels = new JLabel[36];
    SlotAllocator allocator = null;

    public HomeScreen() {
    setTitle("Parking Management System");
    setSize(1000, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Initialize CardLayout and card panel BEFORE using it
    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    // Side menu setup
    JPanel sideMenuPanel = new JPanel();
    sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));

    // Create panels AFTER cardPanel is initialized
    JPanel homePanel = createHomePanel();
    JPanel addVehiclePanel = createAddVehiclePanel();
    JPanel manageVehiclePanel = createManageVehiclePanel();
    JPanel historyPanel = createHistoryPanel();
    JPanel returnSlotPanel = createReturnSlotPanel();

    // Add panels to cardPanel
    cardPanel.add(homePanel, "Home");
    cardPanel.add(addVehiclePanel, "AddVehicle");
    cardPanel.add(manageVehiclePanel, "ManageVehicle");
    cardPanel.add(historyPanel, "History");
    cardPanel.add(returnSlotPanel, "ReturnSlot");

    // Create and add buttons
    JButton btnHome = new JButton("Home");
    JButton btnAddVehicle = new JButton("Add Vehicle");
    JButton btnManageVehicle = new JButton("Manage Vehicle");
    JButton btnHistory = new JButton("History");
    JButton btnReturnSlot = new JButton("Return Slot");

    btnHome.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
    btnAddVehicle.addActionListener(e -> cardLayout.show(cardPanel, "AddVehicle"));
    btnManageVehicle.addActionListener(e -> cardLayout.show(cardPanel, "ManageVehicle"));
    btnHistory.addActionListener(e -> cardLayout.show(cardPanel, "History"));
    btnReturnSlot.addActionListener(e -> cardLayout.show(cardPanel, "ReturnSlot"));

    // Add buttons to side menu
    sideMenuPanel.add(btnHome);
    sideMenuPanel.add(btnAddVehicle);
    sideMenuPanel.add(btnManageVehicle);
    sideMenuPanel.add(btnHistory);
    sideMenuPanel.add(btnReturnSlot);

    // Set layout and add components
    setLayout(new BorderLayout());
    add(sideMenuPanel, BorderLayout.WEST);
    add(cardPanel, BorderLayout.CENTER);

    setVisible(true);

    try {
        allocator = new SlotAllocator("src/parkingsystem/slot.txt");
        System.out.println("File loaded successfully!");
    } catch (IOException ioException) {
        System.out.println("Error reading file: " + ioException.getMessage());
        System.out.println("Please try again.");
    }
    }

    private JPanel createAddVehiclePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField vehicleNumberField = new JTextField();
        JTextField mobileField = new JTextField();
        JComboBox<String> gateComboBox = new JComboBox<>(new String[] { "Gate 1", "Gate 2", "Gate 3" });
        JButton saveButton = new JButton("SAVE VEHICLE");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Vehicle Number:"));
        panel.add(vehicleNumberField);
        panel.add(new JLabel("Mobile No:"));
        panel.add(mobileField);
        panel.add(new JLabel("Choose Gate:"));
        panel.add(gateComboBox);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String vehicleNumber = vehicleNumberField.getText().trim();
                String mobile = mobileField.getText().trim();
                int gateIndex = gateComboBox.getSelectedIndex() + 1; // Changed to 1-based indexing

                // Create vehicle with validation
                Vehicle vehicle = new Vehicle(name, vehicleNumber, mobile, gateIndex);

                // Try to allocate slot with fallback mechanism
                int slotIndex = allocator.getNearestAvailableSlot(gateIndex);

                if (slotIndex != -1) {
                    // Assign slot to vehicle
                    vehicle.assignSlot(slotIndex);
                    parkedVehicles.add(vehicle);

                    // Update slot visualization
                    slotLabels[slotIndex - 1].setBackground(Color.MAGENTA);
                    slotLabels[slotIndex - 1].setText("<html>Slot " + slotIndex + "<br>" + vehicleNumber + "</html>");

                    JOptionPane.showMessageDialog(this,
                            "Vehicle added successfully to Slot " + slotIndex + "!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear input fields
                    nameField.setText("");
                    vehicleNumberField.setText("");
                    mobileField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No available slots in any gate.",
                            "Allocation Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }
    
    private JPanel createReturnSlotPanel() {
    JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JTextField vehicleNumberField = new JTextField();
    JButton returnSlotButton = new JButton("Return Vehicle");
    JTextArea resultArea = new JTextArea();
    resultArea.setEditable(false);
    resultArea.setLineWrap(true);
    resultArea.setWrapStyleWord(true);

    panel.add(new JLabel("Vehicle Number:"));
    panel.add(vehicleNumberField);
    panel.add(new JLabel(" "));
    panel.add(returnSlotButton);
    panel.add(new JLabel("Result:"));
    panel.add(new JScrollPane(resultArea));

    returnSlotButton.addActionListener(e -> {
        String vehicleNumber = vehicleNumberField.getText().trim();

        if (vehicleNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a vehicle number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find and remove the vehicle
        Vehicle vehicleToReturn = parkedVehicles.stream()
                .filter(v -> v.getVehicleNumber().equalsIgnoreCase(vehicleNumber))
                .findFirst()
                .orElse(null);

        if (vehicleToReturn == null) {
            JOptionPane.showMessageDialog(this, "No vehicle found with this number.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the slot number
        int slotNumber = vehicleToReturn.getAssignedSlotIndex();

        if (slotNumber == 0) {
            JOptionPane.showMessageDialog(this, "Vehicle has no assigned slot.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Return the slot and get parking duration
        long parkingDuration = allocator.returnSlot(slotNumber);

        if (parkingDuration != -1) {
            // Update slot visualization
            if (slotNumber >= 1 && slotNumber <= 36) {
                slotLabels[slotNumber - 1].setBackground(Color.LIGHT_GRAY);
                slotLabels[slotNumber - 1].setText("Slot " + slotNumber);
            }

            // Remove vehicle from parked list
            parkedVehicles.remove(vehicleToReturn);

            // Refresh Manage Vehicle panel
            refreshManageVehiclePanel();

            resultArea.setText("Vehicle returned successfully!\n" +
                    "Vehicle Number: " + vehicleNumber + "\n" +
                    "Slot Number: " + slotNumber + "\n" +
                    "Parking Duration: " + parkingDuration + " seconds");

            // Clear input field
            vehicleNumberField.setText("");
        } else {
            resultArea.setText("Failed to return vehicle. The slot might already be available.");
        }
    });

    return panel;
    }
 


    //method to refresh Manage Vehicle panel
    private void refreshManageVehiclePanel() {
        // Find the Manage Vehicle panel in the card layout
        for (Component comp : cardPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;

                // Look for the JTable within this panel
                Component[] subComps = panel.getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) subComp;
                        Component[] tableComps = scrollPane.getComponents();
                        for (Component tableComp : tableComps) {
                            if (tableComp instanceof JTable) {
                                JTable vehicleTable = (JTable) tableComp;
                                DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();

                                // Clear existing rows
                                model.setRowCount(0);

                                // Repopulate with current parked vehicles
                                for (Vehicle vehicle : parkedVehicles) {
                                    model.addRow(new Object[] {
                                        vehicle.getName(),
                                        vehicle.getVehicleNumber(),
                                        vehicle.getMobile(),
                                        "Gate " + (vehicle.getGateIndex()),
                                        vehicle.getAssignedSlotIndex() != 0 ? "Slot " + (vehicle.getAssignedSlotIndex())
                                                : "Not Assigned",
                                        vehicle.getEntryTime().toString()
                                    });
                                }

                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private JPanel createManageVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search components
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Vehicle");

        // Table to display vehicle details
        String[] columnNames = { "Name", "Vehicle Number", "Mobile", "Gate", "Slot", "Entry Time" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable vehicleTable = new JTable(model);

        searchPanel.add(new JLabel("Vehicle Number:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JScrollPane tableScrollPane = new JScrollPane(vehicleTable);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Vehicle tracking logic
        searchButton.addActionListener(e -> {
            String searchNumber = searchField.getText().trim();

            // Clear existing rows
            model.setRowCount(0);

            // Find matching vehicles
            List<Vehicle> matchedVehicles = parkedVehicles.stream()
                    .filter(v -> v.getVehicleNumber().toLowerCase().contains(searchNumber.toLowerCase()))
                    .collect(Collectors.toList());

            if (matchedVehicles.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No vehicles found matching the search.",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Populate table with matching vehicles
            for (Vehicle vehicle : matchedVehicles) {
                model.addRow(new Object[] {
                        vehicle.getName(),
                        vehicle.getVehicleNumber(),
                        vehicle.getMobile(),
                        "Gate " + (vehicle.getGateIndex()),
                        vehicle.getAssignedSlotIndex() != 0 ? "Slot " + (vehicle.getAssignedSlotIndex())
                                : "Not Assigned",
                        vehicle.getEntryTime().toString()
                });
            }
        });

        return panel;
    }

    // Other methods remain the same as in previous implementation
    private JPanel createHomePanel() {
        // Implementation as before
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Add gates
        JLabel gate1 = createGateLabel("Gate 1", Color.RED);
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel.add(gate1, gbc);

        JLabel gate2 = createGateLabel("Gate 2", Color.BLUE);
        gbc.gridx = 7;
        gbc.gridy = 3;
        panel.add(gate2, gbc);

        JLabel gate3 = createGateLabel("Gate 3", Color.GREEN);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(gate3, gbc);

        // Add parking slots
        int slotIndex = 0;
        for (int row = 1; row <= 6; row++) {
            for (int col = 1; col <= 6; col++) {
                JLabel slot = new JLabel("Slot " + (slotIndex + 1), SwingConstants.CENTER);
                slot.setOpaque(true);
                slot.setBackground(Color.LIGHT_GRAY);
                slot.setPreferredSize(new Dimension(60, 60));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                slotLabels[slotIndex] = slot;

                gbc.gridx = col;
                gbc.gridy = row;
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
        gateLabel.setPreferredSize(new Dimension(60, 60)); // Changed from (100, 50) to (60, 60)
        gateLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return gateLabel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("History Panel (to be implemented)"), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new HomeScreen();
        });
    }
}