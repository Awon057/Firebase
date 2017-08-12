package com.example.user.firebaseauthapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.StandaloneActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PriofileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView userEmail;
    private Button loguotButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priofile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        userEmail = (TextView) findViewById(R.id.userEmail);
        userEmail.setText("Welcome " + user.getEmail());
        loguotButton = (Button) findViewById(R.id.logoutButton);

        loguotButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == loguotButton) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
