package com.restaurant.ui;

import com.restaurant.dao.PaymentDAO;
import com.restaurant.model.Payment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaymentPanel extends JPanel {
    private JTextField paymentIdField;
    private JTextField orderIdField;
    private JTextField amountField;
    private JComboBox<String> paymentMethodComboBox;
    private JButton makePaymentButton;

    public PaymentPanel() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Adjusted to fit 5 rows
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Payment ID
        JLabel paymentIdLabel = new JLabel("Payment ID:");
        paymentIdField = new JTextField();

        // Order ID
        JLabel orderIdLabel = new JLabel("Order ID:");
        orderIdField = new JTextField();

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField();

        // Payment Method dropdown
        JLabel methodLabel = new JLabel("Payment Method:");
        paymentMethodComboBox = new JComboBox<>(new String[] {"Cash", "Card", "UPI"});

        // Make Payment button
        makePaymentButton = new JButton("Make Payment");

        // Add components to form panel
        formPanel.add(paymentIdLabel);
        formPanel.add(paymentIdField);
        formPanel.add(orderIdLabel);
        formPanel.add(orderIdField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(methodLabel);
        formPanel.add(paymentMethodComboBox);
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(makePaymentButton);

        // Add form panel to the top of the PaymentPanel
        add(formPanel, BorderLayout.NORTH);

        // Action listener for the make payment button
        makePaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makePayment();
            }
        });
    }

    // Method to handle the payment process
    private void makePayment() {
        try {
            // Parse input values
            String paymentId = paymentIdField.getText().trim();
            String orderId = orderIdField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();

            // Create a Payment object
            Payment payment = new Payment(paymentId, orderId, paymentMethod, amount);

            // Call PaymentDAO to add payment to the database
            boolean success = PaymentDAO.addPayment(payment);

            // Show success or failure message
            if (success) {
                JOptionPane.showMessageDialog(this, "Payment successful!");
                // Clear fields after successful payment
                paymentIdField.setText("");
                orderIdField.setText("");
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Payment failed. Please check the details.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Amount.");
        }
    }
}