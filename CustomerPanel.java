package com.restaurant.ui;

import com.restaurant.dao.CustomerDAO;
import com.restaurant.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for customer management operations
 */
public class CustomerPanel extends JPanel implements ActionListener {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField idField, firstNameField, lastNameField, emailField, phoneField;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton;

    private CustomerDAO customerDAO;

    /**
     * Constructor, initializes the UI
     */
    public CustomerPanel() {
        customerDAO = new CustomerDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadCustomers();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Customer data entry panel
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        dataPanel.add(new JLabel("Customer ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        idField = new JTextField(20);
        idField.setEditable(false);
        dataPanel.add(idField, gbc);

        // First name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        dataPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        firstNameField = new JTextField(20);
        dataPanel.add(firstNameField, gbc);

        // Last name field
        gbc.gridx = 0;
        gbc.gridy = 2;
        dataPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        lastNameField = new JTextField(20);
        dataPanel.add(lastNameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 3;
        dataPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        emailField = new JTextField(20);
        dataPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 4;
        dataPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        phoneField = new JTextField(20);
        dataPanel.add(phoneField, gbc);

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

        // Customer table
        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener to table
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                displaySelectedCustomer();
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Load customer data from database and display in the table
     */
    private void loadCustomers() {
        // Clear the existing data
        tableModel.setRowCount(0);

        // Get all customers from the database
        List<Customer> customers = customerDAO.getAllCustomers();

        // Add customers to the table model
        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhone()
            };
            tableModel.addRow(row);
        }

        // Clear the form fields
        clearForm();
    }

    /**
     * Display the selected customer in the form fields
     */
    private void displaySelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String firstName = tableModel.getValueAt(selectedRow, 1).toString();
            String lastName = tableModel.getValueAt(selectedRow, 2).toString();
            String email = tableModel.getValueAt(selectedRow, 3).toString();
            String phone = tableModel.getValueAt(selectedRow, 4).toString();

            idField.setText(id);
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            emailField.setText(email);
            phoneField.setText(phone);
        }
    }

    /**
     * Clear all form fields
     */
    private void clearForm() {
        idField.setText(customerDAO.getNextCustomerId());
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        customerTable.clearSelection();
    }

    /**
     * Validate form fields
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            MainFrame.showError("First name is required.");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            MainFrame.showError("Last name is required.");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            MainFrame.showError("Email is required.");
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            MainFrame.showError("Phone is required.");
            return false;
        }

        return true;
    }

    /**
     * Create a Customer object from form fields
     * @return Customer object
     */
    private Customer getCustomerFromForm() {
        Customer customer = new Customer();
        customer.setCustomerId(idField.getText());
        customer.setFirstName(firstNameField.getText().trim());
        customer.setLastName(lastNameField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        return customer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (validateForm()) {
                Customer customer = getCustomerFromForm();
                if (customerDAO.addCustomer(customer)) {
                    MainFrame.showInfo("Customer added successfully.");
                    loadCustomers();
                } else {
                    MainFrame.showError("Failed to add customer.");
                }
            }
        } else if (e.getSource() == updateButton) {
            if (validateForm()) {
                Customer customer = getCustomerFromForm();
                if (customerDAO.updateCustomer(customer)) {
                    MainFrame.showInfo("Customer updated successfully.");
                    loadCustomers();
                } else {
                    MainFrame.showError("Failed to update customer.");
                }
            }
        } else if (e.getSource() == deleteButton) {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = tableModel.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this customer?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (customerDAO.deleteCustomer(id)) {
                        MainFrame.showInfo("Customer deleted successfully.");
                        loadCustomers();
                    } else {
                        MainFrame.showError("Failed to delete customer.");
                    }
                }
            } else {
                MainFrame.showError("Please select a customer to delete.");
            }
        } else if (e.getSource() == clearButton) {
            clearForm();
        } else if (e.getSource() == refreshButton) {
            loadCustomers();
        }
    }
}