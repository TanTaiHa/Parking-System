package parkingsystem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsPanel extends JPanel {

    private List<Vehicle> parkedVehicles;

    public StatisticsPanel(List<Vehicle> parkedVehicles) {
        this.parkedVehicles = parkedVehicles;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Parking Gate Statistics", createParkingGatePanel());
        tabbedPane.add("Vehicle Statistics", createVehiclePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createParkingGatePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel lblStartDate = new JLabel("Start Date (yyyy-MM-dd):");
        JLabel lblEndDate = new JLabel("End Date (yyyy-MM-dd):");
        JTextField txtStartDate = new JTextField(10);
        JTextField txtEndDate = new JTextField(10);
        JButton btnGenerate = new JButton("Generate");

        inputPanel.add(lblStartDate);
        inputPanel.add(txtStartDate);
        inputPanel.add(lblEndDate);
        inputPanel.add(txtEndDate);
        inputPanel.add(btnGenerate);

        JPanel resultPanel = new JPanel(new BorderLayout());

        btnGenerate.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(txtStartDate.getText().trim(), DateTimeFormatter.ISO_DATE);
                LocalDate endDate = LocalDate.parse(txtEndDate.getText().trim(), DateTimeFormatter.ISO_DATE);

                // Filter vehicles by date range
                Map<Integer, Long> gateStats = parkedVehicles.stream()
                        .filter(v -> !v.getEntryTime().toLocalDate().isBefore(startDate) &&
                                !v.getEntryTime().toLocalDate().isAfter(endDate))
                        .collect(Collectors.groupingBy(Vehicle::getGateIndex, Collectors.counting()));

                // Create Pie Chart
                DefaultPieDataset dataset = new DefaultPieDataset();
                for (Map.Entry<Integer, Long> entry : gateStats.entrySet()) {
                    dataset.setValue("Gate " + (entry.getKey()), entry.getValue());
                }

                JFreeChart chart = ChartFactory.createPieChart(
                        "Parking Gate Statistics",
                        dataset,
                        true,
                        true,
                        false
                );

                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setSectionPaint("Gate 1", Color.RED);
                plot.setSectionPaint("Gate 2", Color.BLUE);
                plot.setSectionPaint("Gate 3", Color.GREEN);

                resultPanel.removeAll();
                resultPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
                resultPanel.revalidate();
                resultPanel.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input or no data available.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel lblVehicleNumber = new JLabel("Vehicle Number:");
        JTextField txtVehicleNumber = new JTextField(10);
        JLabel lblStartDate = new JLabel("Start Date (yyyy-MM-dd):");
        JLabel lblEndDate = new JLabel("End Date (yyyy-MM-dd):");
        JTextField txtStartDate = new JTextField(10);
        JTextField txtEndDate = new JTextField(10);
        JButton btnGenerate = new JButton("Generate");

        inputPanel.add(lblVehicleNumber);
        inputPanel.add(txtVehicleNumber);
        inputPanel.add(lblStartDate);
        inputPanel.add(txtStartDate);
        inputPanel.add(lblEndDate);
        inputPanel.add(txtEndDate);
        inputPanel.add(btnGenerate);

        JPanel resultPanel = new JPanel(new BorderLayout());

        btnGenerate.addActionListener(e -> {
            try {
                String vehicleNumber = txtVehicleNumber.getText().trim().toLowerCase();
                LocalDate startDate = LocalDate.parse(txtStartDate.getText().trim(), DateTimeFormatter.ISO_DATE);
                LocalDate endDate = LocalDate.parse(txtEndDate.getText().trim(), DateTimeFormatter.ISO_DATE);

                // Filter and group by date
                Map<LocalDate, Long> vehicleStats = parkedVehicles.stream()
                        .filter(v -> v.getVehicleNumber().toLowerCase().equals(vehicleNumber))
                        .filter(v -> !v.getEntryTime().toLocalDate().isBefore(startDate) &&
                                !v.getEntryTime().toLocalDate().isAfter(endDate))
                        .collect(Collectors.groupingBy(v -> v.getEntryTime().toLocalDate(), Collectors.counting()));

                // Create Bar Chart
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (Map.Entry<LocalDate, Long> entry : vehicleStats.entrySet()) {
                    dataset.addValue(entry.getValue(), "Frequency", entry.getKey().toString());
                }

                JFreeChart chart = ChartFactory.createBarChart(
                        "Vehicle Frequency Statistics", // Title
                        "Date", // X-axis label
                        "Frequency", // Y-axis label
                        dataset, // Data
                        PlotOrientation.VERTICAL, // Chart orientation
                        true, // Include legend
                        true, // Tooltips
                        false // URLs
                );

                resultPanel.removeAll();
                resultPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
                resultPanel.revalidate();
                resultPanel.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input or no data available.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);

        return panel;
    }

    public static JPanel createHistoryPanel(List<Vehicle> vehicles) {
        return new StatisticsPanel(vehicles);
    }
}
