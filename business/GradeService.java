package business;

import data.DatabaseConnection;
import data.User;
import presentation.AuthService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeService {

    public static class Grade {
        public int id;
        public int studentId;
        public String courseName;
        public double value;

        public Grade(int id, int studentId, String courseName, double value) {
            this.id = id;
            this.studentId = studentId;
            this.courseName = courseName;
            this.value = value;
        }

        @Override
        public String toString() {
            User student = AuthService.findById(studentId);
            String studentName;
            if (student != null) {
                studentName = student.getName();
            } else {
                studentName = "unknown";
            }
            return id + " | " + studentName + " | " + courseName + " = " + value;
        }
    }

    public static boolean validGrade(double v) {
        return v >= 2.0 && v <= 6.0;
    }

    public static Grade addGrade(int studentId, String courseName, double value) {
        String sql = "INSERT INTO grades (student_id, course_name, value) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, courseName);
            stmt.setDouble(3, value);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return new Grade(newId, studentId, courseName, value);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding grade: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateGrade(int id, double value) {
        String sql = "UPDATE grades SET value = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, value);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating grade: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteGrade(int id) {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting grade: " + e.getMessage());
            return false;
        }
    }

    public static List<Grade> getAllGrades() {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT id, student_id, course_name, value FROM grades ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Grade(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("course_name"),
                    rs.getDouble("value")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading all grades: " + e.getMessage());
        }
        return list;
    }

    public static List<Grade> getGradesByStudent(int studentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT id, student_id, course_name, value FROM grades WHERE student_id = ? ORDER BY course_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Grade(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("course_name"),
                    rs.getDouble("value")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading grades for student: " + e.getMessage());
        }
        return list;
    }

    public static double getAverage(int studentId) {
        String sql = "SELECT AVG(value) FROM grades WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && !rs.wasNull()) {
                double avg = rs.getDouble(1);
                return Math.round(avg * 100.0) / 100.0;
            }
        } catch (SQLException e) {
            System.out.println("Error calculating average: " + e.getMessage());
        }
        return 0;
    }
}