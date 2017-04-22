package com.fs.ps.put.finansominator.model;

import java.math.BigDecimal;
import java.util.List;

public class Category {
    private long id;
    private String name;
    private List<Transaction> relatedTransactions;
    private BigDecimal limit;
    private Budget relatedBudget;

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

    public List<Transaction> getRelatedTransactions() {
        return relatedTransactions;
    }

    public void setRelatedTransactions(List<Transaction> relatedTransactions) {
        this.relatedTransactions = relatedTransactions;
    }

    public Budget getRelatedBudget() {
        return relatedBudget;
    }

    public void setRelatedBudget(Budget relatedBudget) {
        this.relatedBudget = relatedBudget;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return name;
    }
}
