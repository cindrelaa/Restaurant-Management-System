package com.restaurant.ui;

import com.restaurant.dao.CustomerDAO;
import com.restaurant.dao.MenuDAO;
import com.restaurant.dao.OrderDAO;
import com.restaurant.dao.StaffDAO;
import com.restaurant.model.Customer;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for order processing operations
 */
public class OrderPanel extends JPanel implements ActionListener {

    private JTable orderTable;
    private JTable orderItemsTable;
    private DefaultTableModel orderTableModel;
    private DefaultTableModel orderItemsTableModel;

    private JTextField orderIdField;
    private JComboBox<Customer> customerComboBox;
    private JComboBox<Staff> staffComboBox;
    private JLabel totalAmountLabel;

    private JPanel menuItemsPanel;
    private JComboBox<String> categoryComboBox;
    private JComboBox<MenuItem> menuItemComboBox;
    private JSpinner quantitySpinner;

    private JButton addItemButton, removeItemButton;
    private JButton createOrderButton, clearOrderButton, refreshButton;

    private Order currentOrder;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private StaffDAO staffDAO;
    private MenuDAO menuDAO;

    /**
     * Constructor, initializes the UI
     */
    public OrderPanel() {
        orderDAO = new OrderDAO();
        customerDAO = new CustomerDAO();
        staffDAO = new StaffDAO();
        menuDAO = new MenuDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadOrders();
        loadCustomers();
        loadStaff();
        loadCategories();

        // Initialize a new order
        resetOrder();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Split the panel into two sections
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Order creation panel
        JPanel orderCreationPanel = new JPanel(new BorderLayout(10, 10));
        orderCreationPanel.setBorder(BorderFactory.createTitledBorder("Create New Order"));

        // Order details panel
        JPanel orderDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Order ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        orderDetailsPanel.add(new JLabel("Order ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        orderIdField = new JTextField(10);
        orderIdField.setEditable(false);
        orderDetailsPanel.add(orderIdField, gbc);

        // Customer combo box
        gbc.gridx = 0;
        gbc.gridy = 1;
        orderDetailsPanel.add(new JLabel("Customer:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        customerComboBox = new JComboBox<>();
        orderDetailsPanel.add(customerComboBox, gbc);

        // Staff combo box
        gbc.gridx = 0;
        gbc.gridy = 2;
        orderDetailsPanel.add(new JLabel("Staff:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        staffComboBox = new JComboBox<>();
        orderDetailsPanel.add(staffComboBox, gbc);

        // Total amount label
        gbc.gridx = 0;
        gbc.gridy = 3;
        orderDetailsPanel.add(new JLabel("Total Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        totalAmountLabel = new JLabel("₹0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orderDetailsPanel.add(totalAmountLabel, gbc);

        // Menu items selection panel
        menuItemsPanel = new JPanel(new GridBagLayout());
        menuItemsPanel.setBorder(BorderFactory.createTitledBorder("Add Items"));

        // Category combo box
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuItemsPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        categoryComboBox = new JComboBox<>();
        categoryComboBox.addActionListener(this);
        menuItemsPanel.add(categoryComboBox, gbc);

        // Menu item combo box
        gbc.gridx = 0;
        gbc.gridy = 1;
        menuItemsPanel.add(new JLabel("Item:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        menuItemComboBox = new JComboBox<>();
        menuItemsPanel.add(menuItemComboBox, gbc);

        // Quantity spinner
        gbc.gridx = 0;
        gbc.gridy = 2;
        menuItemsPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        menuItemsPanel.add(quantitySpinner, gbc);

        // Add item button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addItemButton = new JButton("Add Item to Order");
        addItemButton.addActionListener(this);
        menuItemsPanel.add(addItemButton, gbc);

        // Remove item button
        gbc.gridx = 0;
        gbc.gridy = 4;
        removeItemButton = new JButton("Remove Selected Item");
        removeItemButton.addActionListener(this);
        menuItemsPanel.add(removeItemButton, gbc);

        // Order creation buttons panel
        JPanel orderButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        createOrderButton = new JButton("Create Order");
        createOrderButton.addActionListener(this);
        orderButtonsPanel.add(createOrderButton);

        clearOrderButton = new JButton("Clear Order");
        clearOrderButton.addActionListener(this);
        orderButtonsPanel.add(clearOrderButton);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        orderButtonsPanel.add(refreshButton);

        // Combine all panels for order creation
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(orderDetailsPanel, BorderLayout.NORTH);
        leftPanel.add(menuItemsPanel, BorderLayout.CENTER);

        // Order items table
        String[] orderItemsColumns = {"Item Name", "Category", "Price", "Quantity", "Subtotal"};
        orderItemsTableModel = new DefaultTableModel(orderItemsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderItemsTable = new JTable(orderItemsTableModel);
        JScrollPane orderItemsScrollPane = new JScrollPane(orderItemsTable);
        orderItemsScrollPane.setBorder(BorderFactory.createTitledBorder("Order Items"));

        // Combine left panel and order items table
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.add(leftPanel);
        topPanel.add(orderItemsScrollPane);

        // Add all components to the order creation panel
        orderCreationPanel.add(topPanel, BorderLayout.CENTER);
        orderCreationPanel.add(orderButtonsPanel, BorderLayout.SOUTH);

        // Orders table
        String[] orderColumns = {"Order ID", "Customer", "Staff", "Order Date", "Total Amount"};
        orderTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setBorder(BorderFactory.createTitledBorder("Orders"));

        // Add both panels to the split pane
        splitPane.setTopComponent(orderCreationPanel);
        splitPane.setBottomComponent(orderScrollPane);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Reset the current order
     */
    private void resetOrder() {
        currentOrder = new Order();
        currentOrder.setOrderId(orderDAO.getNextOrderId());
        orderIdField.setText(currentOrder.getOrderId());

        // Clear the order items table
        orderItemsTableModel.setRowCount(0);

        // Update the total
        updateTotal();
    }

    /**
     * Update the total amount label
     */
    private void updateTotal() {
        currentOrder.calculateTotal();
        totalAmountLabel.setText("₹" + String.format("%.2f", currentOrder.getTotalAmount()));
    }

    /**
     * Load all orders from the database
     */
    private void loadOrders() {
        // Clear the existing data
        orderTableModel.setRowCount(0);

        // Get all orders from the database
        List<Order> orders = orderDAO.getAllOrders();

        // Add orders to the table model
        for (Order order : orders) {
            Customer customer = customerDAO.getCustomerById(order.getCustomerId());
            Staff staff = staffDAO.getStaffById(order.getStaffId());

            Object[] row = {
                    order.getOrderId(),
                    customer.getFullName(),
                    staff.getFullName(),
                    order.getOrderDate(),
                    "₹" + String.format("%.2f", order.getTotalAmount())
            };
            orderTableModel.addRow(row);
        }
    }

    /**
     * Load all customers into the customers combo box
     */
    private void loadCustomers() {
        customerComboBox.removeAllItems();

        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
        }
    }

    /**
     * Load all waiters into the staff combo box
     */
    private void loadStaff() {
        staffComboBox.removeAllItems();

        List<Staff> waiters = staffDAO.getStaffByRole("Waiter");
        for (Staff waiter : waiters) {
            staffComboBox.addItem(waiter);
        }
    }

    /**
     * Load all categories into the category combo box
     */
    private void loadCategories() {
        categoryComboBox.removeAllItems();

        List<String> categories = menuDAO.getAllCategories();
        for (String category : categories) {
            categoryComboBox.addItem(category);
        }

        // Load menu items for the first category if available
        if (categoryComboBox.getItemCount() > 0) {
            loadMenuItemsByCategory((String) categoryComboBox.getSelectedItem());
        }
    }

    /**
     * Load menu items for a specific category
     * @param category Category to load items for
     */
    private void loadMenuItemsByCategory(String category) {
        menuItemComboBox.removeAllItems();

        List<MenuItem> menuItems = menuDAO.getMenuItemsByCategory(category);
        for (MenuItem item : menuItems) {
            menuItemComboBox.addItem(item);
        }
    }

    /**
     * Add a menu item to the current order
     */
    private void addItemToOrder() {
        if (menuItemComboBox.getSelectedItem() == null) {
            MainFrame.showError("Please select a menu item.");
            return;
        }

        MenuItem menuItem = (MenuItem) menuItemComboBox.getSelectedItem();
        int quantity = (int) quantitySpinner.getValue();

        // Create a new order item
        OrderItem orderItem = new OrderItem(currentOrder.getOrderId(), menuItem, quantity);

        // Add the item to the order
        currentOrder.addItem(orderItem);

        // Update the order items table
        Object[] row = {
                menuItem.getName(),
                menuItem.getCategory(),
                "₹" + menuItem.getPrice(),
                quantity,
                "₹" + String.format("%.2f", orderItem.getSubtotal())
        };
        orderItemsTableModel.addRow(row);

        // Update the total
        updateTotal();
    }

    /**
     * Remove the selected item from the current order
     */
    private void removeItemFromOrder() {
        int selectedRow = orderItemsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the item name from the table
            String itemName = (String) orderItemsTableModel.getValueAt(selectedRow, 0);

            // Find and remove the item from the order
            for (int i = 0; i < currentOrder.getOrderItems().size(); i++) {
                OrderItem item = currentOrder.getOrderItems().get(i);
                if (item.getMenuItem().getName().equals(itemName)) {
                    currentOrder.removeItem(item);
                    break;
                }
            }

            // Remove the row from the table
            orderItemsTableModel.removeRow(selectedRow);

            // Update the total
            updateTotal();
        } else {
            MainFrame.showError("Please select an item to remove.");
        }
    }

    /**
     * Create a new order in the database
     */
    private void createOrder() {
        if (currentOrder.getOrderItems().isEmpty()) {
            MainFrame.showError("Cannot create an empty order. Please add items first.");
            return;
        }

        if (customerComboBox.getSelectedItem() == null) {
            MainFrame.showError("Please select a customer.");
            return;
        }

        if (staffComboBox.getSelectedItem() == null) {
            MainFrame.showError("Please select a staff member.");
            return;
        }

        // Set the customer and staff for the order
        Customer customer = (Customer) customerComboBox.getSelectedItem();
        Staff staff = (Staff) staffComboBox.getSelectedItem();

        currentOrder.setCustomerId(customer.getCustomerId());
        currentOrder.setStaffId(staff.getStaffId());

        // Save the order to the database
        if (orderDAO.addOrder(currentOrder)) {
            MainFrame.showInfo("Order created successfully.");
            loadOrders();
            resetOrder();
        } else {
            MainFrame.showError("Failed to create order.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == categoryComboBox) {
            // Load menu items for the selected category
            String category = (String) categoryComboBox.getSelectedItem();
            if (category != null) {
                loadMenuItemsByCategory(category);
            }
        } else if (e.getSource() == addItemButton) {
            addItemToOrder();
        } else if (e.getSource() == removeItemButton) {
            removeItemFromOrder();
        } else if (e.getSource() == createOrderButton) {
            createOrder();
        } else if (e.getSource() == clearOrderButton) {
            resetOrder();
        } else if (e.getSource() == refreshButton) {
            loadOrders();
            loadCustomers();
            loadStaff();
            loadCategories();
        }
    }
}