package com.restaurant.dao;

import com.restaurant.model.Customer;
import com.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer entity
 */
public class CustomerDAO {

    /**
     * Get all customers from the database
     * @return List of customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Customer")) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("C_ID"),
                        rs.getString("C_fname"),
                        rs.getString("C_lname"),
                        rs.getString("C_email1"),
                        rs.getString("C_Phno1")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Get customer by ID
     * @param customerId Customer ID
     * @return Customer object or null if not found
     */
    public Customer getCustomerById(String customerId) {
        Customer customer = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Customer WHERE C_ID = ?")) {

            pstmt.setString(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer(
                            rs.getString("C_ID"),
                            rs.getString("C_fname"),
                            rs.getString("C_lname"),
                            rs.getString("C_email1"),
                            rs.getString("C_Phno1")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customer: " + e.getMessage());
        }

        return customer;
    }

    /**
     * Add a new customer to the database
     * @param customer Customer to add
     * @return true if successful, false otherwise
     */
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (C_ID, C_fname, C_lname, C_email1, C_Phno1) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getPhone());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing customer in the database
     * @param customer Customer to update
     * @return true if successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customer SET C_fname = ?, C_lname = ?, C_email1 = ?, C_Phno1 = ? WHERE C_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getCustomerId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a customer from the database
     * @param customerId ID of the customer to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCustomer(String customerId) {
        String sql = "DELETE FROM Customer WHERE C_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the next available customer ID
     * @return Next available customer ID
     */
    public String getNextCustomerId() {
        String lastId = "C000";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(C_ID) AS max_id FROM Customer")) {

            if (rs.next() && rs.getString("max_id") != null) {
                lastId = rs.getString("max_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting last customer ID: " + e.getMessage());
        }

        // Extract numeric part and increment
        int idNum = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("C%03d", idNum);
    }
}
