package com.restaurant.dao;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order and OrderItem entities
 */
public class OrderDAO {
    private MenuDAO menuDAO = new MenuDAO();

    /**
     * Get all orders from the database
     * @return List of orders
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Orders")) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getString("Order_ID"),
                        rs.getString("C_ID"),
                        rs.getString("S_ID")
                );
                order.setOrderDate(rs.getDate("Order_Date"));
                order.setTotalAmount(rs.getDouble("Total_Amt"));

                // Get order items for this order
                order.setOrderItems(getOrderItemsForOrder(order.getOrderId()));

                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Get order by ID
     * @param orderId Order ID
     * @return Order object or null if not found
     */
    public Order getOrderById(String orderId) {
        Order order = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Orders WHERE Order_ID = ?")) {

            pstmt.setString(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = new Order(
                            rs.getString("Order_ID"),
                            rs.getString("C_ID"),
                            rs.getString("S_ID")
                    );
                    order.setOrderDate(rs.getDate("Order_Date"));
                    order.setTotalAmount(rs.getDouble("Total_Amt"));

                    // Get order items for this order
                    order.setOrderItems(getOrderItemsForOrder(order.getOrderId()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving order: " + e.getMessage());
        }

        return order;
    }

    /**
     * Get all order items for a specific order
     * @param orderId Order ID
     * @return List of order items
     */
    public List<OrderItem> getOrderItemsForOrder(String orderId) {
        List<OrderItem> orderItems = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Order_Items WHERE Order_ID = ?")) {

            pstmt.setString(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String itemName = rs.getString("I_Name");
                    int quantity = rs.getInt("Quantity");

                    OrderItem orderItem = new OrderItem(
                            orderId,
                            menuDAO.getMenuItemByName(itemName),
                            quantity
                    );
                    orderItems.add(orderItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving order items: " + e.getMessage());
        }

        return orderItems;
    }

    /**
     * Add a new order to the database
     * @param order Order to add
     * @return true if successful, false otherwise
     */
    public boolean addOrder(Order order) {
        String orderSql = "INSERT INTO Orders (Order_ID, C_ID, S_ID, Order_Date, Total_Amt) VALUES (?, ?, ?, ?, ?)";
        String itemsSql = "INSERT INTO Order_Items (Order_ID, I_Name, Quantity) VALUES (?, ?, ?)";

        Connection conn = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert the order
            try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
                pstmt.setString(1, order.getOrderId());
                pstmt.setString(2, order.getCustomerId());
                pstmt.setString(3, order.getStaffId());
                pstmt.setDate(4, new java.sql.Date(order.getOrderDate().getTime()));
                pstmt.setDouble(5, order.getTotalAmount());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    // Insert the order items
                    try (PreparedStatement itemsPstmt = conn.prepareStatement(itemsSql)) {
                        for (OrderItem item : order.getOrderItems()) {
                            itemsPstmt.setString(1, order.getOrderId());
                            itemsPstmt.setString(2, item.getMenuItem().getName());
                            itemsPstmt.setInt(3, item.getQuantity());
                            itemsPstmt.addBatch();
                        }
                        itemsPstmt.executeBatch();
                    }

                    conn.commit();
                    success = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        return success;
    }

    /**
     * Get orders for a specific customer
     * @param customerId Customer ID
     * @return List of orders for the customer
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Orders WHERE C_ID = ?")) {

            pstmt.setString(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getString("Order_ID"),
                            rs.getString("C_ID"),
                            rs.getString("S_ID")
                    );
                    order.setOrderDate(rs.getDate("Order_Date"));
                    order.setTotalAmount(rs.getDouble("Total_Amt"));

                    // Get order items for this order
                    order.setOrderItems(getOrderItemsForOrder(order.getOrderId()));

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving orders by customer: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Get the next available order ID
     * @return Next available order ID
     */
    public String getNextOrderId() {
        String lastId = "O000";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(Order_ID) AS max_id FROM Orders")) {

            if (rs.next() && rs.getString("max_id") != null) {
                lastId = rs.getString("max_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting last order ID: " + e.getMessage());
        }

        // Extract numeric part and increment
        int idNum = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("O%03d", idNum);
    }
}