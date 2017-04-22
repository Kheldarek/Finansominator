package com.fs.ps.put.finansominator.model;

import java.util.List;

public class Budget {

    private long id;
    private String name;
    private User owner;
    private List<Category> availableCategories;
    private List<Transaction> relatedTransactions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Category> getAvailableCategories() {
        return availableCategories;
    }

    public void setAvailableCategories(List<Category> availableCategories) {
        this.availableCategories = availableCategories;
    }

    public List<Transaction> getRelatedTransactions() {
        return relatedTransactions;
    }

    public void setRelatedTransactions(List<Transaction> relatedTransactions) {
        this.relatedTransactions = relatedTransactions;
    }

    @Override
    public String toString() {
        return name;
    }
}
