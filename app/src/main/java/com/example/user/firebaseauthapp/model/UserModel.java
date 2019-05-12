package com.example.user.firebaseauthapp.model;

public class UserModel {
    public String name;
    public String email;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)


    public UserModel() {
    }

    public UserModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
