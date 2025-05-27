package com.restaurant.model;

/**
 * OrderItem model class representing the Order_Items table in the database
 */
public class OrderItem {
    private String orderId;
    private MenuItem menuItem;
    private int quantity;

    // Default constructor
    public OrderItem() {
    }

    // Constructor with all fields
    public OrderItem(String orderId, MenuItem menuItem, int quantity) {
        this.orderId = orderId;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Calculate subtotal for this order item
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity + " = ₹" + getSubtotal();
    }
}