package com.ragnar.splitwise.GroupFragment;

import java.util.List;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private List<String> members;
    private Double amountToBePaid;  // Amount to be paid by each member
    private List<String> balances;

    // Constructors, getters, and setters
    public Group() {
        // Default constructor required for Firestore
    }

    public Group(String id, String name, List<String> members, Double amountToBePaid, List<String> balances) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.amountToBePaid = amountToBePaid;
        this.balances = balances;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(Double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }
    public List<String> getBalances() {
        return balances;
    }
    public void setBalances(List<String> balances) {
        this.balances = balances;
    }
}
