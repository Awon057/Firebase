package com.example.user.firebaseauthapp;

import com.google.firebase.database.FirebaseDatabase;

public class ApplicationClass extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
