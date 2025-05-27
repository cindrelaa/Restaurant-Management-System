package com.restaurant.ui;

import com.restaurant.dao.*;
import com.restaurant.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dashboard panel displaying key metrics and summary information
 */
public class DashboardPanel extends JPanel {

    private JLabel dateTimeLabel;
    private JLabel totalCustomersLabel;
    private JLabel totalStaffLabel;
    private JLabel totalMenuItemsLabel;
    private JLabel totalOrdersLabel;
    private JLabel totalRevenueLabel;

    private Timer refreshTimer;

    /**
     * Constructor, initializes the UI
     */
    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadData();

        // Set up a timer to refresh the dashboard every 60 seconds
        refreshTimer = new Timer(60000, e -> loadData());
        refreshTimer.start();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Restaurant Management System Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(dateTimeLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Summary statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Summary Statistics",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        totalCustomersLabel = createStatsLabel("Total Customers");
        totalStaffLabel = createStatsLabel("Total Staff");
        totalMenuItemsLabel = createStatsLabel("Menu Items");
        totalOrdersLabel = createStatsLabel("Total Orders");
        totalRevenueLabel = createStatsLabel("Total Revenue");

        statsPanel.add(totalCustomersLabel);
        statsPanel.add(totalStaffLabel);
        statsPanel.add(totalMenuItemsLabel);
        statsPanel.add(totalOrdersLabel);
        statsPanel.add(totalRevenueLabel);

        add(statsPanel, BorderLayout.CENTER);

        // Recent activities panel
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Recent Activities",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        JTextArea recentActivitiesArea = new JTextArea(10, 40);
        recentActivitiesArea.setEditable(false);
        recentActivitiesArea.setText("System initialized at " + getCurrentDateTime());

        JScrollPane scrollPane = new JScrollPane(recentActivitiesArea);
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        add(recentPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a panel for displaying a single statistic
     * @param title Title of the statistic
     * @return JLabel for the statistic
     */
    private JLabel createStatsLabel(String title) {
        JLabel label = new JLabel(title + ": Loading...");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)));
        return label;
    }

    /**
     * Get the current date and time as a formatted string
     * @return Formatted date and time
     */
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * Load data from the database and update the dashboard
     */
    private void loadData() {
        dateTimeLabel.setText("Last updated: " + getCurrentDateTime());

        try {
            // Get counts from the database
            Connection conn = DatabaseConnection.getConnection();

            // Count of customers
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer")) {
                if (rs.next()) {
                    totalCustomersLabel.setText("Total Customers: " + rs.getInt(1));
                }
            }

            // Count of staff
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Staff")) {
                if (rs.next()) {
                    totalStaffLabel.setText("Total Staff: " + rs.getInt(1));
                }
            }

            // Count of menu items
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Menu")) {
                if (rs.next()) {
                    totalMenuItemsLabel.setText("Menu Items: " + rs.getInt(1));
                }
            }

            // Count of orders
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Orders")) {
                if (rs.next()) {
                    totalOrdersLabel.setText("Total Orders: " + rs.getInt(1));
                }
            }

            // Sum of all payments (total revenue)
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SUM(Amount) FROM Payment")) {
                if (rs.next()) {
                    double total = rs.getDouble(1);
                    totalRevenueLabel.setText("Total Revenue: ₹" + String.format("%.2f", total));
                }
            }

        } catch (SQLException e) {
            MainFrame.showError("Error loading dashboard data: " + e.getMessage());
        }
    }

    /**
     * Called when the panel is no longer visible
     */
    public void stopTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
}