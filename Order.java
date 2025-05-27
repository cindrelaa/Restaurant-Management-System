package com.restaurant.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Order model class representing the Orders table in the database
 */
public class Order {
    private String orderId;
    private String customerId;
    private String staffId;
    private Date orderDate;
    private double totalAmount;
    private List<OrderItem> orderItems;

    // Default constructor
    public Order() {
        this.orderItems = new ArrayList<>();
        this.orderDate = new Date();
    }

    // Constructor with essential fields
    public Order(String orderId, String customerId, String staffId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.orderDate = new Date();
        this.orderItems = new ArrayList<>();
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Add an item to the order
    public void addItem(OrderItem item) {
        this.orderItems.add(item);
        calculateTotal();
    }

    // Remove an item from the order
    public void removeItem(OrderItem item) {
        this.orderItems.remove(item);
        calculateTotal();
    }

    // Calculate total amount based on order items
    public void calculateTotal() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getMenuItem().getPrice() * item.getQuantity();
        }
        this.totalAmount = total;
    }
}