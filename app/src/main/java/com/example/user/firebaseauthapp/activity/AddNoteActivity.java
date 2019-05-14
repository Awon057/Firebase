package com.example.user.firebaseauthapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText title;
    private EditText details;
    private Button saveButton;
    private FirebaseDatabase mInstance;
    private DatabaseReference mDatabaseTable;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Toolbar mTopToolbar;
    private NotesWrapperModel wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mTopToolbar = (Toolbar) findViewById(R.id.add_notes_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setTitle("Add Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (EditText) findViewById(R.id.title);
        details = (EditText) findViewById(R.id.details);
        saveButton = (Button) findViewById(R.id.saveNote);

        wrapper = (NotesWrapperModel) getIntent().getSerializableExtra("model");
        if (wrapper.getId() != null) {
            initialize();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mInstance = FirebaseDatabase.getInstance();
        mDatabaseTable = mInstance.getReference("notes");

        saveButton.setOnClickListener(this);
    }

    private void initialize() {
        title.setText(wrapper.getNotesModel().getTitle());
        details.setText(wrapper.getNotesModel().getDetails());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveNote:
                NotesModel notesModel = new NotesModel();
                notesModel.setTitle(title.getText().toString());
                notesModel.setDetails(details.getText().toString());

                if (wrapper.getId() != null) {
                    updateNotes(notesModel);
                } else
                    saveNotes(notesModel);
                break;
        }
    }

    private void updateNotes(NotesModel notesModel) {
        try {
            mDatabaseTable.child(user.getUid()).child(wrapper.getId()).setValue(notesModel);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNotes(NotesModel notesModel) {
        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
        String userId = mDatabaseTable.push().getKey();
        // pushing user to 'notes' node using the userId
        mDatabaseTable.child(user.getUid()).child(userId).setValue(notesModel);
        finish();
    }
}
