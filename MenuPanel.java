package com.restaurant.ui;

import com.restaurant.dao.MenuDAO;
import com.restaurant.model.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for menu management operations
 */
public class MenuPanel extends JPanel implements ActionListener {

    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, categoryField, priceField;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton;
    private JComboBox<String> categoryComboBox;

    private MenuDAO menuDAO;
    private String originalName; // For updates

    /**
     * Constructor, initializes the UI
     */
    public MenuPanel() {
        menuDAO = new MenuDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadMenuItems();
        loadCategories();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Menu item data entry panel
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Menu Item Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        dataPanel.add(new JLabel("Item Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        nameField = new JTextField(20);
        dataPanel.add(nameField, gbc);

        // Category field
        gbc.gridx = 0;
        gbc.gridy = 1;
        dataPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        categoryField = new JTextField(20);
        dataPanel.add(categoryField, gbc);

        // Category dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        dataPanel.add(new JLabel("Select Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        categoryComboBox = new JComboBox<>();
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String)categoryComboBox.getSelectedItem();
            if (selectedCategory != null && !selectedCategory.equals("-- New Category --")) {
                categoryField.setText(selectedCategory);
            }
        });
        dataPanel.add(categoryComboBox, gbc);

        // Price field
        gbc.gridx = 0;
        gbc.gridy = 3;
        dataPanel.add(new JLabel("Price (₹):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        priceField = new JTextField(20);
        dataPanel.add(priceField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        buttonPanel.add(clearButton);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        buttonPanel.add(refreshButton);

        // Form panel (data entry + buttons)
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(dataPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.NORTH);

        // Menu table
        String[] columnNames = {"Item Name", "Category", "Price (₹)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        menuTable = new JTable(tableModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener to table
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() != -1) {
                displaySelectedMenuItem();
            }
        });

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Menu Items"));

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Load menu items from database and display in the table
     */
    private void loadMenuItems() {
        // Clear the existing data
        tableModel.setRowCount(0);

        // Get all menu items from the database
        List<MenuItem> menuItems = menuDAO.getAllMenuItems();

        // Add menu items to the table model
        for (MenuItem item : menuItems) {
            Object[] row = {
                    item.getName(),
                    item.getCategory(),
                    item.getPrice()
            };
            tableModel.addRow(row);
        }

        // Clear the form fields
        clearForm();
    }

    /**
     * Load all categories into the category combo box
     */
    private void loadCategories() {
        categoryComboBox.removeAllItems();
        categoryComboBox.addItem("-- New Category --");

        List<String> categories = menuDAO.getAllCategories();
        for (String category : categories) {
            categoryComboBox.addItem(category);
        }
    }

    /**
     * Display the selected menu item in the form fields
     */
    private void displaySelectedMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = tableModel.getValueAt(selectedRow, 0).toString();
            String category = tableModel.getValueAt(selectedRow, 1).toString();
            String price = tableModel.getValueAt(selectedRow, 2).toString();

            originalName = name;
            nameField.setText(name);
            categoryField.setText(category);
            priceField.setText(price);

            // Select the category in the combo box if it exists
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                if (categoryComboBox.getItemAt(i).equals(category)) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Clear all form fields
     */
    private void clearForm() {
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        categoryComboBox.setSelectedIndex(0);
        menuTable.clearSelection();
        originalName = null;
    }

    /**
     * Validate form fields
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            MainFrame.showError("Item name is required.");
            return false;
        }

        if (categoryField.getText().trim().isEmpty()) {
            MainFrame.showError("Category is required.");
            return false;
        }

        if (priceField.getText().trim().isEmpty()) {
            MainFrame.showError("Price is required.");
            return false;
        }

        try {
            int price = Integer.parseInt(priceField.getText().trim());
            if (price <= 0) {
                MainFrame.showError("Price must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            MainFrame.showError("Price must be a valid number.");
            return false;
        }

        return true;
    }

    /**
     * Create a MenuItem object from form fields
     * @return MenuItem object
     */
    private MenuItem getMenuItemFromForm() {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(nameField.getText().trim());
        menuItem.setCategory(categoryField.getText().trim());
        menuItem.setPrice(Integer.parseInt(priceField.getText().trim()));
        return menuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (validateForm()) {
                MenuItem menuItem = getMenuItemFromForm();
                if (menuDAO.addMenuItem(menuItem)) {
                    MainFrame.showInfo("Menu item added successfully.");
                    loadMenuItems();
                    loadCategories();
                } else {
                    MainFrame.showError("Failed to add menu item.");
                }
            }
        } else if (e.getSource() == updateButton) {
            if (validateForm() && originalName != null) {
                MenuItem menuItem = getMenuItemFromForm();
                if (menuDAO.updateMenuItem(menuItem, originalName)) {
                    MainFrame.showInfo("Menu item updated successfully.");
                    loadMenuItems();
                    loadCategories();
                } else {
                    MainFrame.showError("Failed to update menu item.");
                }
            } else if (originalName == null) {
                MainFrame.showError("Please select a menu item to update.");
            }
        } else if (e.getSource() == deleteButton) {
            int selectedRow = menuTable.getSelectedRow();
            if (selectedRow != -1) {
                String name = tableModel.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this menu item?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (menuDAO.deleteMenuItem(name)) {
                        MainFrame.showInfo("Menu item deleted successfully.");
                        loadMenuItems();
                    } else {
                        MainFrame.showError("Failed to delete menu item.");
                    }
                }
            } else {
                MainFrame.showError("Please select a menu item to delete.");
            }
        } else if (e.getSource() == clearButton) {
            clearForm();
        } else if (e.getSource() == refreshButton) {
            loadMenuItems();
            loadCategories();
        }
    }
}