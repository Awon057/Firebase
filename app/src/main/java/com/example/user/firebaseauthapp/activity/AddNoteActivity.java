package com.example.user.firebaseauthapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText title;
    private EditText details;
    private Button saveButton;
    private FirebaseDatabase mInstance;
    private DatabaseReference mDatabaseTable;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title = (EditText)findViewById(R.id.title);
        details = (EditText) findViewById(R.id.details);
        saveButton = (Button) findViewById(R.id.saveNote);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mInstance = FirebaseDatabase.getInstance();
        mDatabaseTable = mInstance.getReference("notes");

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveNote:
                saveNotes();
                break;
        }
    }

    private void saveNotes() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("notes");

        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
        String userId = mDatabase.push().getKey();

        // creating user object
        NotesModel notesModel = new NotesModel();
        notesModel.setTitle(title.getText().toString());
        notesModel.setDetails(details.getText().toString());

        // pushing user to 'notes' node using the userId
        mDatabase.child(user.getUid()).child(userId).setValue(notesModel);
        finish();
    }
}
