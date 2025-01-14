package com.cs309.websocket3.Admins;

import jakarta.persistence.*;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String employeeId;
    private String password;

    // Constructors
    public Admin(String name, String employeeId, String password) {
        this.name = name;
        this.employeeId = employeeId;
        this.password = password;
    }

    public Admin() {
    }

    // =============================== Getters and Setters for each field ================================== //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
