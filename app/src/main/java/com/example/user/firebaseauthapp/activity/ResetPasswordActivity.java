package com.example.user.firebaseauthapp.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.firebaseauthapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    private Toolbar mTopToolbar;
    private EditText resetEmail;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mTopToolbar = (Toolbar) findViewById(R.id.reset_pass_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resetEmail = (EditText) findViewById(R.id.reset_password_email);
        resetButton = (Button) findViewById(R.id.reset_button);

        resetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_button:
                if (resetEmail != null && resetEmail.getText().length() > 0) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(resetEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                    }else
                                        Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else
                    Toast.makeText(ResetPasswordActivity.this, "Please Fill email", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
