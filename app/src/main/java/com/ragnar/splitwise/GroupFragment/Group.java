package com.ragnar.splitwise.GroupFragment;

import java.util.List;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private List<String> members;
    private Double totalAmount;  // Total amount for the group
    private Double amountToBePaid;  // Amount to be paid by each member

    // Constructors, getters, and setters
    public Group() {
        // Default constructor required for Firestore
    }

    public Group(String id, String name, List<String> members, Double totalAmount, Double amountToBePaid) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.totalAmount = totalAmount;
        this.amountToBePaid = amountToBePaid;
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(Double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }
}
