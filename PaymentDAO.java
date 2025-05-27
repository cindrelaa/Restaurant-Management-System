package com.restaurant.dao;

import com.restaurant.model.Payment;
import com.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payment entity
 */
public class PaymentDAO {

    /**
     * Get all payments from the database
     * @return List of payments
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Payment")) {

            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getString("P_ID"),
                        rs.getString("Order_ID"),
                        rs.getString("P_Method"),
                        rs.getDouble("Amount")
                );
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
        }

        return payments;
    }

    /**
     * Get payment by ID
     * @param paymentId Payment ID
     * @return Payment object or null if not found
     */
    public Payment getPaymentById(String paymentId) {
        Payment payment = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Payment WHERE P_ID = ?")) {

            pstmt.setString(1, paymentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment = new Payment(
                            rs.getString("P_ID"),
                            rs.getString("Order_ID"),
                            rs.getString("P_Method"),
                            rs.getDouble("Amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
        }

        return payment;
    }

    /**
     * Get payment by order ID
     * @param orderId Order ID
     * @return Payment object or null if not found
     */
    public Payment getPaymentByOrderId(String orderId) {
        Payment payment = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Payment WHERE Order_ID = ?")) {

            pstmt.setString(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment = new Payment(
                            rs.getString("P_ID"),
                            rs.getString("Order_ID"),
                            rs.getString("P_Method"),
                            rs.getDouble("Amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment by order ID: " + e.getMessage());
        }

        return payment;
    }

    /**
     * Add a new payment to the database
     * @param payment Payment to add
     * @return true if successful, false otherwise
     */
    public static boolean addPayment(Payment payment) {
        String sql = "INSERT INTO Payment (P_ID, Order_ID, P_Method, Amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, payment.getPaymentId());
            pstmt.setString(2, payment.getOrderId());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setDouble(4, payment.getAmount());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing payment in the database
     * @param payment Payment to update
     * @return true if successful, false otherwise
     */
    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE Payment SET Order_ID = ?, P_Method = ?, Amount = ? WHERE P_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, payment.getOrderId());
            pstmt.setString(2, payment.getPaymentMethod());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a payment from the database
     * @param paymentId ID of the payment to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePayment(String paymentId) {
        String sql = "DELETE FROM Payment WHERE P_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paymentId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the next available payment ID
     * @return Next available payment ID
     */
    public String getNextPaymentId() {
        String lastId = "P000";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(P_ID) AS max_id FROM Payment")) {

            if (rs.next() && rs.getString("max_id") != null) {
                lastId = rs.getString("max_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting last payment ID: " + e.getMessage());
        }

        // Extract numeric part and increment
        int idNum = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("P%03d", idNum);
    }
}