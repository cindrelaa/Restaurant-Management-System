package com.restaurant.model;

import java.util.Date;

/**
 * Staff model class representing the Staff table in the database
 */
public class Staff {
    private String staffId;
    private String firstName;
    private String lastName;
    private String role;
    private String phone;
    private Date dateOfBirth;
    private double salary;

    // Default constructor
    public Staff() {
    }

    // Constructor with all fields
    public Staff(String staffId, String firstName, String lastName, String role,
                 String phone, Date dateOfBirth, double salary) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
    }

    // Getters and setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return staffId + " - " + getFullName() + " (" + role + ")";
    }
}