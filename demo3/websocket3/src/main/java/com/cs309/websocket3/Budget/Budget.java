package com.cs309.websocket3.Budget;

import jakarta.persistence.*;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double expenses;
    private Double income;
    private Double expexp;
    private Double expdif;
    private String month;

    // Field to store the ID of the associated User
    private Integer userId;

    public Budget(Double expenses, Double income, double expexp, String month) {
        this.expenses = expenses;
        this.income = income;
        this.expexp = expexp;
        this.expdif = expexp - expenses;
        this.month = month;
    }

    public Budget() {
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getExpenses() {
        return expenses;
    }

    public void setExpenses(Double expenses) {
        this.expenses = expenses;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpexp() {
        return expexp;
    }
    public void setExpexp(Double expexp) {
        this.expexp = expexp;
    }
    public Double getExpdif() {
        return expdif;
    }
    public void setExpdif(Double expdif) {
        this.expdif = expdif;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }

}

