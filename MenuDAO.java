package com.restaurant.dao;

import com.restaurant.model.MenuItem;
import com.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MenuItem entity
 */
public class MenuDAO {

    /**
     * Get all menu items from the database
     * @return List of menu items
     */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Menu")) {

            while (rs.next()) {
                MenuItem menuItem = new MenuItem(
                        rs.getString("I_Name"),
                        rs.getString("Category"),
                        rs.getInt("Price")
                );
                menuItems.add(menuItem);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving menu items: " + e.getMessage());
        }

        return menuItems;
    }

    /**
     * Get menu items by category
     * @param category Category to filter by
     * @return List of menu items in the specified category
     */
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Menu WHERE Category = ?")) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem menuItem = new MenuItem(
                            rs.getString("I_Name"),
                            rs.getString("Category"),
                            rs.getInt("Price")
                    );
                    menuItems.add(menuItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving menu items by category: " + e.getMessage());
        }

        return menuItems;
    }

    /**
     * Get all menu categories
     * @return List of unique categories
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT Category FROM Menu ORDER BY Category")) {

            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
        }

        return categories;
    }

    /**
     * Get menu item by name
     * @param name Menu item name
     * @return MenuItem object or null if not found
     */
    public MenuItem getMenuItemByName(String name) {
        MenuItem menuItem = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Menu WHERE I_Name = ?")) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    menuItem = new MenuItem(
                            rs.getString("I_Name"),
                            rs.getString("Category"),
                            rs.getInt("Price")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving menu item: " + e.getMessage());
        }

        return menuItem;
    }

    /**
     * Add a new menu item to the database
     * @param menuItem MenuItem to add
     * @return true if successful, false otherwise
     */
    public boolean addMenuItem(MenuItem menuItem) {
        String sql = "INSERT INTO Menu (I_Name, Category, Price) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, menuItem.getName());
            pstmt.setString(2, menuItem.getCategory());
            pstmt.setInt(3, menuItem.getPrice());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing menu item in the database
     * @param menuItem MenuItem to update
     * @return true if successful, false otherwise
     */
    public boolean updateMenuItem(MenuItem menuItem, String originalName) {
        String sql = "UPDATE Menu SET I_Name = ?, Category = ?, Price = ? WHERE I_Name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, menuItem.getName());
            pstmt.setString(2, menuItem.getCategory());
            pstmt.setInt(3, menuItem.getPrice());
            pstmt.setString(4, originalName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a menu item from the database
     * @param name Name of the menu item to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteMenuItem(String name) {
        String sql = "DELETE FROM Menu WHERE I_Name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting menu item: " + e.getMessage());
            return false;
        }
    }
}