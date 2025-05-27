package com.restaurant.dao;

import com.restaurant.model.Staff;
import com.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Staff entity
 */
public class StaffDAO {

    /**
     * Get all staff from the database
     * @return List of staff
     */
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Staff")) {

            while (rs.next()) {
                Staff staff = new Staff(
                        rs.getString("S_ID"),
                        rs.getString("SF_Name"),
                        rs.getString("SL_Name"),
                        rs.getString("Role"),
                        rs.getString("S_Phone1"),
                        rs.getDate("DOB"),
                        rs.getDouble("Salary")
                );
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff: " + e.getMessage());
        }

        return staffList;
    }

    /**
     * Get staff by ID
     * @param staffId Staff ID
     * @return Staff object or null if not found
     */
    public Staff getStaffById(String staffId) {
        Staff staff = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Staff WHERE S_ID = ?")) {

            pstmt.setString(1, staffId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    staff = new Staff(
                            rs.getString("S_ID"),
                            rs.getString("SF_Name"),
                            rs.getString("SL_Name"),
                            rs.getString("Role"),
                            rs.getString("S_Phone1"),
                            rs.getDate("DOB"),
                            rs.getDouble("Salary")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff: " + e.getMessage());
        }

        return staff;
    }

    /**
     * Add a new staff to the database
     * @param staff Staff to add
     * @return true if successful, false otherwise
     */
    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO Staff (S_ID, SF_Name, SL_Name, Role, S_Phone1, DOB, Salary) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, staff.getStaffId());
            pstmt.setString(2, staff.getFirstName());
            pstmt.setString(3, staff.getLastName());
            pstmt.setString(4, staff.getRole());
            pstmt.setString(5, staff.getPhone());
            pstmt.setDate(6, new java.sql.Date(staff.getDateOfBirth().getTime()));
            pstmt.setDouble(7, staff.getSalary());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding staff: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing staff in the database
     * @param staff Staff to update
     * @return true if successful, false otherwise
     */
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE Staff SET SF_Name = ?, SL_Name = ?, Role = ?, S_Phone1 = ?, DOB = ?, Salary = ? WHERE S_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getRole());
            pstmt.setString(4, staff.getPhone());
            pstmt.setDate(5, new java.sql.Date(staff.getDateOfBirth().getTime()));
            pstmt.setDouble(6, staff.getSalary());
            pstmt.setString(7, staff.getStaffId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a staff from the database
     * @param staffId ID of the staff to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteStaff(String staffId) {
        String sql = "DELETE FROM Staff WHERE S_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, staffId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get staff by role
     * @param role Role to filter by
     * @return List of staff with the specified role
     */
    public List<Staff> getStaffByRole(String role) {
        List<Staff> staffList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Staff WHERE Role = ?")) {

            pstmt.setString(1, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Staff staff = new Staff(
                            rs.getString("S_ID"),
                            rs.getString("SF_Name"),
                            rs.getString("SL_Name"),
                            rs.getString("Role"),
                            rs.getString("S_Phone1"),
                            rs.getDate("DOB"),
                            rs.getDouble("Salary")
                    );
                    staffList.add(staff);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff by role: " + e.getMessage());
        }

        return staffList;
    }

    /**
     * Get the next available staff ID
     * @return Next available staff ID
     */
    public String getNextStaffId() {
        String lastId = "S000";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(S_ID) AS max_id FROM Staff")) {

            if (rs.next() && rs.getString("max_id") != null) {
                lastId = rs.getString("max_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting last staff ID: " + e.getMessage());
        }

        // Extract numeric part and increment
        int idNum = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("S%03d", idNum);
    }
}