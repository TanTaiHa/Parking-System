package parkingsystem;

import java.sql.*;
import java.time.LocalDateTime;

public class DBOperation {
    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_parking";
    private static final String USER = "root";
    private static final String PASSWORD = "huynhhuunghia";

    // Initialize database and create table if not exists
    static {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            String createTableSQL = """
                        CREATE TABLE IF NOT EXISTS parking_records (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100),
                            vehicle_number VARCHAR(20),
                            mobile VARCHAR(15),
                            gate_number INT,
                            slot_number INT,
                            entry_time TIMESTAMP,
                            exit_time TIMESTAMP NULL,
                            duration_seconds BIGINT NULL
                        )
                    """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveVehicleEntry(Vehicle vehicle) {
        String sql = """
                    INSERT INTO parking_records
                    (name, vehicle_number, mobile, gate_number, slot_number, entry_time)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getName());
            pstmt.setString(2, vehicle.getVehicleNumber());
            pstmt.setString(3, vehicle.getMobile());
            pstmt.setInt(4, vehicle.getGateIndex());
            pstmt.setInt(5, vehicle.getAssignedSlotIndex());
            pstmt.setTimestamp(6, Timestamp.valueOf(vehicle.getEntryTime()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateVehicleExit(String vehicleNumber, long durationSeconds) {
        String sql = """
                    UPDATE parking_records
                    SET exit_time = ?, duration_seconds = ?
                    WHERE vehicle_number = ? AND exit_time IS NULL
                """;

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(2, durationSeconds);
            pstmt.setString(3, vehicleNumber);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getVehicleHistory(String vehicleNumber) {
        String sql = """
                    SELECT * FROM parking_records
                    WHERE vehicle_number LIKE ?
                    ORDER BY entry_time DESC
                """;

        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + vehicleNumber + "%");
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getAllParkingRecords() {
        String sql = "SELECT * FROM parking_records ORDER BY entry_time DESC";

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}