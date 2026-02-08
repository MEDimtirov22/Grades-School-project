package presentation;

import data.DatabaseConnection;
import data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    public static int register(String name, String role, String password) {
        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            return -2;
        }
        role = role.toLowerCase();
        if (!role.equals("student") && !role.equals("teacher")) {
            return -1;
        }

        String sqlCheck = "SELECT COUNT(*) FROM users WHERE name = ? AND role = ?";
        String sqlInsert = "INSERT INTO users (name, role, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(sqlCheck);
             PreparedStatement insert = conn.prepareStatement(sqlInsert)) {

            check.setString(1, name);
            check.setString(2, role);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return 0;
            }

            insert.setString(1, name);
            insert.setString(2, role);
            insert.setString(3, password);
            insert.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return -3;
        }
    }

    public static User login(String name, String password, String role) {
        String sql = "SELECT id, name, role, password FROM users WHERE name = ? AND role = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, role.toLowerCase());
            stmt.setString(3, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return null;
    }

    public static boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public static boolean changePassword(int id, String newPass) {
        if (newPass == null || newPass.isBlank()) {
            return false;
        }
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPass);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    public static List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, role, password FROM users ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return list;
    }

    public static User findById(int id) {
        String sql = "SELECT id, name, role, password FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }
}