package com.fs.ps.put.finansominator.listAdapters.beans;

/**
 * Created by Kheldar on 14-Jan-17.
 */

public class GroupBudgetBean {
    public String name;
    public String owner;
    public String members;
    public String balance;

    public GroupBudgetBean() {
    }

    public GroupBudgetBean(String name, String owner, String members, String balance) {
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.balance = balance;
    }
}
