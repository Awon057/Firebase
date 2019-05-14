package com.example.user.firebaseauthapp.model;

import java.io.Serializable;

public class NotesWrapperModel implements Serializable {
    String id;
    NotesModel notesModel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotesModel getNotesModel() {
        return notesModel;
    }

    public void setNotesModel(NotesModel notesModel) {
        this.notesModel = notesModel;
    }
}
