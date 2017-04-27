package com.fs.ps.put.finansominator.model;

import java.util.List;

public class User {
    private long id;
    private String username;
    private String email;
    private byte[] password;
    private List<Budget> ownedBudgets;
    private byte[] salt;

    public User() {
    }

    public User(String username, String email, byte[] password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public List<Budget> getOwnedBudget() {
        return ownedBudgets;
    }

    public void setOwnedBudget(List<Budget> ownedBudgets) {
        this.ownedBudgets = ownedBudgets;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }
}
