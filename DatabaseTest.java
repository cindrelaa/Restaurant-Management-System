package com.restaurant.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("Database is connected successfully!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
            DatabaseConnection.closeConnection();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
