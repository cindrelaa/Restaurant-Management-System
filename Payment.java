package com.restaurant.model;

/**
 * Payment model class representing the Payment table in the database
 */
public class Payment {
    private String paymentId;
    private String orderId;
    private String paymentMethod;
    private double amount;

    // Default constructor
    public Payment() {
    }

    // Constructor with all fields
    public Payment(String paymentId, String orderId, String paymentMethod, double amount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    // Getters and setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return paymentId + " - Order: " + orderId + " - ₹" + amount + " (" + paymentMethod + ")";
    }
}