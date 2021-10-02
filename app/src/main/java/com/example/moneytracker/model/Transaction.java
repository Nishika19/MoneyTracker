package com.example.moneytracker.model;

import java.util.Date;

public class Transaction {
    private Date date;
    private String userId;
    private String username;
    private String category;
    private int Amount;
    private String note;
    private String type;
    private String month;

    public Transaction() {
    }

    public Transaction(String category, int amount) {
        this.category = category;
        Amount = amount;
    }

    public Transaction(Date date, String userId, String username, String category,
                       int amount, String note, String type, String month) {
        this.date = date;
        this.userId = userId;
        this.username = username;
        this.category = category;
        Amount = amount;
        this.note = note;
        this.type = type;
        this.month = month;
    }

    public Transaction(String month) {
        this.month = month;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
