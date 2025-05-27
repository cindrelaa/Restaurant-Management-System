package com.restaurant.ui;

import com.restaurant.dao.StaffDAO;
import com.restaurant.model.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Panel for staff management operations
 */
public class StaffPanel extends JPanel implements ActionListener {

    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField idField, firstNameField, lastNameField, phoneField, salaryField;
    private JComboBox<String> roleComboBox;
    private JTextField dobField;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton;

    private StaffDAO staffDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Constructor, initializes the UI
     */
    public StaffPanel() {
        staffDAO = new StaffDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadStaff();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Staff data entry panel
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Staff Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        dataPanel.add(new JLabel("Staff ID:"), gbc);

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

        // Role field
        gbc.gridx = 0;
        gbc.gridy = 3;
        dataPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        String[] roles = {"Manager", "Chef", "Waiter", "Receptionist", "Cashier", "Cleaner", "Security", "Bartender"};
        roleComboBox = new JComboBox<>(roles);
        dataPanel.add(roleComboBox, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 4;
        dataPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        phoneField = new JTextField(20);
        dataPanel.add(phoneField, gbc);

        // DOB field
        gbc.gridx = 0;
        gbc.gridy = 5;
        dataPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        dobField = new JTextField(20);
        dataPanel.add(dobField, gbc);

        // Salary field
        gbc.gridx = 0;
        gbc.gridy = 6;
        dataPanel.add(new JLabel("Salary:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        salaryField = new JTextField(20);
        dataPanel.add(salaryField, gbc);

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

        // Staff table
        String[] columnNames = {"Staff ID", "First Name", "Last Name", "Role", "Phone", "DOB", "Salary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener to table
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && staffTable.getSelectedRow() != -1) {
                displaySelectedStaff();
            }
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Staff"));

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Load staff data from database and display in the table
     */
    private void loadStaff() {
        // Clear the existing data
        tableModel.setRowCount(0);

        // Get all staff from the database
        List<Staff> staffList = staffDAO.getAllStaff();

        // Add staff to the table model
        for (Staff staff : staffList) {
            Object[] row = {
                    staff.getStaffId(),
                    staff.getFirstName(),
                    staff.getLastName(),
                    staff.getRole(),
                    staff.getPhone(),
                    dateFormat.format(staff.getDateOfBirth()),
                    staff.getSalary()
            };
            tableModel.addRow(row);
        }

        // Clear the form fields
        clearForm();
    }

    /**
     * Display the selected staff in the form fields
     */
    private void displaySelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String firstName = tableModel.getValueAt(selectedRow, 1).toString();
            String lastName = tableModel.getValueAt(selectedRow, 2).toString();
            String role = tableModel.getValueAt(selectedRow, 3).toString();
            String phone = tableModel.getValueAt(selectedRow, 4).toString();
            String dob = tableModel.getValueAt(selectedRow, 5).toString();
            String salary = tableModel.getValueAt(selectedRow, 6).toString();

            idField.setText(id);
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);

            // Select the role in the combo box
            for (int i = 0; i < roleComboBox.getItemCount(); i++) {
                if (roleComboBox.getItemAt(i).equals(role)) {
                    roleComboBox.setSelectedIndex(i);
                    break;
                }
            }

            phoneField.setText(phone);
            dobField.setText(dob);
            salaryField.setText(salary);
        }
    }

    /**
     * Clear all form fields
     */
    private void clearForm() {
        idField.setText(staffDAO.getNextStaffId());
        firstNameField.setText("");
        lastNameField.setText("");
        roleComboBox.setSelectedIndex(0);
        phoneField.setText("");
        dobField.setText("");
        salaryField.setText("");
        staffTable.clearSelection();
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

        if (phoneField.getText().trim().isEmpty()) {
            MainFrame.showError("Phone is required.");
            return false;
        }

        if (dobField.getText().trim().isEmpty()) {
            MainFrame.showError("Date of Birth is required.");
            return false;
        }

        try {
            dateFormat.parse(dobField.getText().trim());
        } catch (ParseException e) {
            MainFrame.showError("Date of Birth must be in the format yyyy-MM-dd.");
            return false;
        }

        if (salaryField.getText().trim().isEmpty()) {
            MainFrame.showError("Salary is required.");
            return false;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            if (salary <= 0) {
                MainFrame.showError("Salary must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            MainFrame.showError("Salary must be a valid number.");
            return false;
        }

        return true;
    }

    /**
     * Create a Staff object from form fields
     * @return Staff object
     */
    private Staff getStaffFromForm() {
        Staff staff = new Staff();
        staff.setStaffId(idField.getText());
        staff.setFirstName(firstNameField.getText().trim());
        staff.setLastName(lastNameField.getText().trim());
        staff.setRole((String) roleComboBox.getSelectedItem());
        staff.setPhone(phoneField.getText().trim());

        try {
            Date dob = dateFormat.parse(dobField.getText().trim());
            staff.setDateOfBirth(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        staff.setSalary(Double.parseDouble(salaryField.getText().trim()));
        return staff;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (validateForm()) {
                Staff staff = getStaffFromForm();
                if (staffDAO.addStaff(staff)) {
                    MainFrame.showInfo("Staff added successfully.");
                    loadStaff();
                } else {
                    MainFrame.showError("Failed to add staff.");
                }
            }
        } else if (e.getSource() == updateButton) {
            if (validateForm()) {
                Staff staff = getStaffFromForm();
                if (staffDAO.updateStaff(staff)) {
                    MainFrame.showInfo("Staff updated successfully.");
                    loadStaff();
                } else {
                    MainFrame.showError("Failed to update staff.");
                }
            }
        } else if (e.getSource() == deleteButton) {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = tableModel.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this staff member?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (staffDAO.deleteStaff(id)) {
                        MainFrame.showInfo("Staff deleted successfully.");
                        loadStaff();
                    } else {
                        MainFrame.showError("Failed to delete staff.");
                    }
                }
            } else {
                MainFrame.showError("Please select a staff member to delete.");
            }
        } else if (e.getSource() == clearButton) {
            clearForm();
        } else if (e.getSource() == refreshButton) {
            loadStaff();
        }
    }
}
