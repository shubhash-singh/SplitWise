package com.ragnar.splitwise.GroupFragment;

public class User {
    private String name, userID, phoneNumber;

    // Empty constructor for Firestore
    public User() {}

    public User(String name) {
        this.name =  name;
    }

    public String getName() {
        return name;
    }
    public String getUserID(){
        return userID;
    }
    public String getPhoneNumber(){
        return  phoneNumber;
    }
}

