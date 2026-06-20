package CitizeenComplaintSystem.repository;

import CitizeenComplaintSystem.model.Complaint;
import CitizeenComplaintSystem.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLComplaintRepository implements ComplaintRepository {

    @Override
    public void save(Complaint c) {
        String checkQuery = "SELECT COUNT(*) FROM complaints WHERE complaint_id = ?";
        String insertQuery = "INSERT INTO complaints (complaint_id, title, description, category, ward, affected_people, severity, priority_score, status, citizen_username, assigned_employee_username, days_pending) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE complaints SET title = ?, description = ?, category = ?, ward = ?, affected_people = ?, severity = ?, priority_score = ?, status = ?, citizen_username = ?, assigned_employee_username = ?, days_pending = ? WHERE complaint_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, c.getComplaintId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, c.getTitle());
                    updateStmt.setString(2, c.getDescription());
                    updateStmt.setString(3, c.getCategory());
                    updateStmt.setString(4, c.getWard());
                    updateStmt.setInt(5, c.getAffectedPeople());
                    updateStmt.setInt(6, c.getSeverity());
                    updateStmt.setInt(7, c.getPriorityScore());
                    updateStmt.setString(8, c.getStatus());
                    updateStmt.setString(9, c.getCitizenUsername());
                    updateStmt.setString(10, c.getAssignedEmployeeUsername());
                    updateStmt.setInt(11, c.getDaysPending());
                    updateStmt.setString(12, c.getComplaintId());
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, c.getComplaintId());
                    insertStmt.setString(2, c.getTitle());
                    insertStmt.setString(3, c.getDescription());
                    insertStmt.setString(4, c.getCategory());
                    insertStmt.setString(5, c.getWard());
                    insertStmt.setInt(6, c.getAffectedPeople());
                    insertStmt.setInt(7, c.getSeverity());
                    insertStmt.setInt(8, c.getPriorityScore());
                    insertStmt.setString(9, c.getStatus());
                    insertStmt.setString(10, c.getCitizenUsername());
                    insertStmt.setString(11, c.getAssignedEmployeeUsername());
                    insertStmt.setInt(12, c.getDaysPending());
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error saving complaint: " + e.getMessage());
        }
    }

    @Override
    public Complaint findById(String id) {
        String query = "SELECT * FROM complaints WHERE complaint_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding complaint: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Complaint> findAll() {
        List<Complaint> list = new ArrayList<>();
        String query = "SELECT * FROM complaints";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(mapRowToComplaint(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error loading all complaints: " + e.getMessage());
        }
        return list;
    }

    private Complaint mapRowToComplaint(ResultSet rs) throws SQLException {
        return new Complaint(
                rs.getString("complaint_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("ward"),
                rs.getInt("affected_people"),
                rs.getInt("severity"),
                rs.getInt("priority_score"),
                rs.getString("status"),
                rs.getString("citizen_username"),
                rs.getString("assigned_employee_username"),
                rs.getInt("days_pending")
        );
    }
}
