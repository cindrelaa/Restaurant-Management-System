package com.restaurant.model;

/**
 * MenuItem model class representing the Menu table in the database
 */
public class MenuItem {
    private String name;
    private String category;
    private int price;

    // Default constructor
    public MenuItem() {
    }

    // Constructor with all fields
    public MenuItem(String name, String category, int price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " (" + category + ") - ₹" + price;
    }
}