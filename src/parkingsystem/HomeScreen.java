package parkingsystem;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
         // Load trạng thái phương tiện chưa trả slot
         loadUnreturnedVehicles();
        
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
        try (Connection conn = DriverManager.getConnection(DBOperation.URL, DBOperation.USER, DBOperation.PASSWORD)) {
            String name = nameField.getText().trim();
            String vehicleNumber = vehicleNumberField.getText().trim();
            String mobile = mobileField.getText().trim();
            int gateIndex = gateComboBox.getSelectedIndex() + 1;

            // Kiểm tra phương tiện đã tồn tại
            boolean vehicleExists = parkedVehicles.stream()
                    .anyMatch(v -> v.getVehicleNumber().equalsIgnoreCase(vehicleNumber));

            if (vehicleExists) {
                JOptionPane.showMessageDialog(this, "Vehicle with this number is already parked.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Lấy slot gần nhất
            int slotIndex = allocator.getNearestAvailableSlot(gateIndex);
            if (slotIndex == -1) {
                JOptionPane.showMessageDialog(this, "No available slots in any gate.", "Allocation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Lưu phương tiện vào cơ sở dữ liệu và lấy ID
            String sql = "INSERT INTO vehicles (name, vehicle_number, mobile, gate_index, slot_index, entry_time, taken_vehicle) " +
                         "VALUES (?, ?, ?, ?, ?, NOW(), TRUE)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setString(2, vehicleNumber);
                stmt.setString(3, mobile);
                stmt.setInt(4, gateIndex);
                stmt.setInt(5, slotIndex);
                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);

                    // Tạo đối tượng Vehicle và thêm vào danh sách
                    Vehicle vehicle = new Vehicle(id, name, vehicleNumber, mobile, gateIndex);
                    vehicle.assignSlot(slotIndex);
                    parkedVehicles.add(vehicle);

                    // Cập nhật giao diện
                    slotLabels[slotIndex - 1].setBackground(Color.MAGENTA);
                    slotLabels[slotIndex - 1].setText("<html>Slot " + slotIndex + "<br>" + vehicleNumber + "</html>");

                    JOptionPane.showMessageDialog(this, "Vehicle added successfully to Slot " + slotIndex + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            nameField.setText("");
            vehicleNumberField.setText("");
            mobileField.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add vehicle. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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

        Vehicle vehicleToReturn = parkedVehicles.stream()
                .filter(v -> v.getVehicleNumber().equalsIgnoreCase(vehicleNumber))
                .findFirst()
                .orElse(null);

        if (vehicleToReturn == null) {
            JOptionPane.showMessageDialog(this, "No vehicle found with this number.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int slotNumber = vehicleToReturn.getAssignedSlotIndex();
        if (slotNumber == 0) {
            JOptionPane.showMessageDialog(this, "Vehicle has no assigned slot.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DBOperation.URL, DBOperation.USER, DBOperation.PASSWORD)) {
            long parkingDuration = allocator.returnSlot(slotNumber);

            String sql = "UPDATE vehicles SET taken_vehicle = FALSE, exit_time = NOW(), duration = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, (int) parkingDuration);
                stmt.setInt(2, vehicleToReturn.getId()); // Dùng ID thay vì vehicle_number
                stmt.executeUpdate();
            }

            slotLabels[slotNumber - 1].setBackground(Color.LIGHT_GRAY);
            slotLabels[slotNumber - 1].setText("Slot " + slotNumber);

            parkedVehicles.remove(vehicleToReturn);
            refreshManageVehiclePanel();

            resultArea.setText("Vehicle returned successfully!\n" +
                    "Vehicle ID: " + vehicleToReturn.getId() + "\n" +
                    "Vehicle Number: " + vehicleNumber + "\n" +
                    "Slot Number: " + slotNumber + "\n" +
                    "Parking Duration: " + parkingDuration + " seconds");

            vehicleNumberField.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to return vehicle. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

        return panel;
    }


    // method to refresh Manage Vehicle panel
    private void refreshManageVehiclePanel() {
        JPanel manageVehiclePanel = null;

        // Tìm đúng panel "ManageVehicle"
        for (Component comp : cardPanel.getComponents()) {
            if (comp instanceof JPanel && cardPanel.getComponentZOrder(comp) == 2) { // Vị trí 2 là ManageVehicle
                manageVehiclePanel = (JPanel) comp;
                break;
            }
        }

        if (manageVehiclePanel == null) {
            System.out.println("Manage Vehicle Panel not found!");
            return;
        }

        // Lấy JTable từ Manage Vehicle Panel
        JScrollPane scrollPane = (JScrollPane) manageVehiclePanel.getComponent(1); // ScrollPane là phần tử thứ 2
        JTable vehicleTable = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();

        // Xóa các hàng cũ
        model.setRowCount(0);

        // Thêm các phương tiện hiện tại vào bảng
        for (Vehicle vehicle : parkedVehicles) {
            model.addRow(new Object[] {
                vehicle.getName(),
                vehicle.getVehicleNumber(),
                vehicle.getMobile(),
                "Gate " + vehicle.getGateIndex(),
                vehicle.getAssignedSlotIndex() != 0 ? "Slot " + vehicle.getAssignedSlotIndex() : "Not Assigned",
                vehicle.getEntryTime() != null ? vehicle.getEntryTime().toString() : "N/A"
            });
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

        // Table to display vehicle history
        String[] columnNames = { "Name", "Vehicle Number", "Mobile", "Gate", "Slot", "Entry Time", "Exit Time", "Duration", "Return the slot" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable historyTable = new JTable(model);

        JScrollPane tableScrollPane = new JScrollPane(historyTable);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");

        // Add action listener to the refresh button
        refreshButton.addActionListener(e -> {
            // Clear the table
            model.setRowCount(0);

            // Fetch data from the database
            try (Connection conn = DriverManager.getConnection(DBOperation.URL, DBOperation.USER, DBOperation.PASSWORD)) {
                String sql = "SELECT name, vehicle_number, mobile, gate_index, slot_index, entry_time, exit_time, duration, taken_vehicle FROM vehicles";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    while (rs.next()) {
                        String name = rs.getString("name");
                        String vehicleNumber = rs.getString("vehicle_number");
                        String mobile = rs.getString("mobile");
                        int gateIndex = rs.getInt("gate_index");
                        int slotIndex = rs.getInt("slot_index");
                        Timestamp entryTime = rs.getTimestamp("entry_time");
                        Timestamp exitTime = rs.getTimestamp("exit_time");
                        int duration = rs.getInt("duration");
                        boolean takenVehicle = rs.getBoolean("taken_vehicle");

                        model.addRow(new Object[] {
                                name,
                                vehicleNumber,
                                mobile,
                                "Gate " + gateIndex,
                                slotIndex != 0 ? "Slot " + slotIndex : "Not Assigned",
                                entryTime != null ? entryTime.toString() : "N/A",
                                exitTime != null ? exitTime.toString() : "N/A",
                                duration > 0 ? duration + " seconds" : "N/A",
                                takenVehicle ? "No" : "Yes"
                        });
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Failed to fetch vehicle history. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Trigger initial load
        refreshButton.doClick();

        // Add components to the panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private void loadUnreturnedVehicles() {
        try (Connection conn = DriverManager.getConnection(DBOperation.URL, DBOperation.USER, DBOperation.PASSWORD)) {
            String sql = "SELECT id, name, vehicle_number, mobile, gate_index, slot_index, entry_time " +
                         "FROM vehicles WHERE taken_vehicle = TRUE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String vehicleNumber = rs.getString("vehicle_number");
                    String mobile = rs.getString("mobile");
                    int gateIndex = rs.getInt("gate_index");
                    int slotIndex = rs.getInt("slot_index");
                    Timestamp entryTime = rs.getTimestamp("entry_time");

                    // Tạo đối tượng Vehicle
                    Vehicle vehicle = new Vehicle(id, name, vehicleNumber, mobile, gateIndex);
                    vehicle.assignSlot(slotIndex);
                    // Convert entryTime to milliseconds
//                    long entryTimeMillis = entryTime.getTime();
                    allocator.getSlotID(slotIndex, entryTime.getTime());

                    // Thêm vào danh sách và cập nhật giao diện
                    parkedVehicles.add(vehicle);
                    slotLabels[slotIndex - 1].setBackground(Color.MAGENTA);
                    slotLabels[slotIndex - 1].setText("<html>Slot " + slotIndex + "<br>" + vehicleNumber + "</html>");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load vehicle data. Please check the database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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