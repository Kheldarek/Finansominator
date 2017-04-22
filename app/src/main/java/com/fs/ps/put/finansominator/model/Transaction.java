package com.fs.ps.put.finansominator.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    private long id;
    private Category transactionCategory;
    private BigDecimal amount;
    private Date transactionDate;
    private TransactionType transactionType;
    private Budget relatedBudget;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(Category transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Budget getRelatedBudget() {
        return relatedBudget;
    }

    public void setRelatedBudget(Budget relatedBudget) {
        this.relatedBudget = relatedBudget;
    }
}
