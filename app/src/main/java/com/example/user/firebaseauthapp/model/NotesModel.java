package com.example.user.firebaseauthapp.model;

import java.io.Serializable;

public class NotesModel implements Serializable {
    String title;
    String details;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
