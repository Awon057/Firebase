package com.example.user.firebaseauthapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText loginEmail;
    private EditText loginPassword;
    private TextView signin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            //profile Activity
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        progressDialog = new ProgressDialog(this);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        signin = (TextView) findViewById(R.id.textViewSignUp);

        buttonSignIn.setOnClickListener(this);
        signin.setOnClickListener(this);

    }

    private void userLogin() {
        String email = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Loging in");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSignIn) {
            userLogin();
        }
        if (v == signin) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
