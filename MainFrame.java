package com.restaurant.ui;

import com.restaurant.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application frame containing all components of the Restaurant Management System
 */
public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    // Tab panels
    private DashboardPanel dashboardPanel;
    private CustomerPanel customerPanel;
    private MenuPanel menuPanel;
    private StaffPanel staffPanel;
    private OrderPanel orderPanel;
    private PaymentPanel paymentPanel;

    /**
     * Constructor, initializes the UI
     */
    public MainFrame() {
        setTitle("Restaurant Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));

        // Add window closing event to close the database connection
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
                super.windowClosing(e);
            }
        });

        initComponents();
        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Set the look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabbedPane = new JTabbedPane();

        try {
            dashboardPanel = new DashboardPanel();
            tabbedPane.addTab("Dashboard", null, dashboardPanel, "Main Dashboard");
        } catch (Exception e) {
            System.err.println("Failed to load DashboardPanel: " + e.getMessage());
        }

        try {
            customerPanel = new CustomerPanel();
            tabbedPane.addTab("Customers", null, customerPanel, "Manage Customers");
        } catch (Exception e) {
            System.err.println("Failed to load CustomerPanel: " + e.getMessage());
        }

        try {
            menuPanel = new MenuPanel();
            tabbedPane.addTab("Menu", null, menuPanel, "Manage Menu Items");
        } catch (Exception e) {
            System.err.println("Failed to load MenuPanel: " + e.getMessage());
        }

        try {
            staffPanel = new StaffPanel();
            tabbedPane.addTab("Staff", null, staffPanel, "Manage Staff");
        } catch (Exception e) {
            System.err.println("Failed to load StaffPanel: " + e.getMessage());
        }

        try {
            orderPanel = new OrderPanel();
            tabbedPane.addTab("Orders", null, orderPanel, "Process Orders");
        } catch (Exception e) {
            System.err.println("Failed to load OrderPanel: " + e.getMessage());
        }

        try {
            paymentPanel = new PaymentPanel();
            tabbedPane.addTab("Payments", null, paymentPanel, "Process Payments");
        } catch (Exception e) {
            System.err.println("Failed to load PaymentPanel: " + e.getMessage());
        }

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Create a status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * Show an error message dialog
     */
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an information message dialog
     */
    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.getConnection();
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                showError("Failed to connect to database: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}
