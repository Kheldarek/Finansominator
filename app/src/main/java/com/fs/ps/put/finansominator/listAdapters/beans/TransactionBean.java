package com.fs.ps.put.finansominator.listAdapters.beans;

/**
 * Created by Kheldar on 14-Jan-17.
 */

public class TransactionBean {
    public String budget;

    public String amount;
    public String date;
    public String category;
    TransactionBean(){}

    public TransactionBean(String budget, String amount, String date, String category) {
        this.budget = budget;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }


}
