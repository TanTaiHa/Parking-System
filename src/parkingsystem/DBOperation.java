package parkingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DBOperation {
    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_parking";
    private static final String USER = "root";
    private static final String PASSWORD = "hiimtantaI010103";

    private Connection connect() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean adminLogin(String username, String password) {
        try (Connection conn = connect()) {
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void resetDatabase(String adminUsername, String adminPassword, int totalSpaces) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Drop existing tables
            stmt.execute("DROP TABLE IF EXISTS admin");
            stmt.execute("DROP TABLE IF EXISTS parking_space");

            // Recreate admin table and insert initial admin
            stmt.execute("CREATE TABLE admin (username VARCHAR(255), password VARCHAR(255))");
            PreparedStatement adminStmt = conn.prepareStatement("INSERT INTO admin (username, password) VALUES (?, ?)");
            adminStmt.setString(1, adminUsername);
            adminStmt.setString(2, adminPassword);
            adminStmt.executeUpdate();

            // Create parking_space table without type column
            stmt.execute("CREATE TABLE parking_space (available_spaces INT)");
            PreparedStatement spaceStmt = conn
                    .prepareStatement("INSERT INTO parking_space (available_spaces) VALUES (?)");
            spaceStmt.setInt(1, totalSpaces);
            spaceStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
