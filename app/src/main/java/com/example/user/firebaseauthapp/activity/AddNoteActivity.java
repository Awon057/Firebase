package com.example.user.firebaseauthapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int MY_TITLE_CHECK_CODE = 101;
    private static final int MY_DETAILS_CHECK_CODE = 102;
    private EditText title;
    private EditText details;
    private Button saveButton;
    private FirebaseDatabase mInstance;
    private DatabaseReference mDatabaseTable;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Toolbar mTopToolbar;
    private NotesWrapperModel wrapper;
    private ImageButton speakTitle;
    private ImageButton speakDetails;

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
        speakTitle = (ImageButton) findViewById(R.id.btn_speak_title);
        speakDetails = (ImageButton) findViewById(R.id.btn_speak_details);

        wrapper = (NotesWrapperModel) getIntent().getSerializableExtra("model");
        if (wrapper.getId() != null) {
            initialize();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mInstance = FirebaseDatabase.getInstance();
        mDatabaseTable = mInstance.getReference("notes");

        saveButton.setOnClickListener(this);
        speakTitle.setVisibility(View.GONE);
        speakDetails.setVisibility(View.GONE);
        /*speakTitle.setOnClickListener(this);
        speakDetails.setOnClickListener(this);*/

        title.setOnTouchListener(this);
        details.setOnTouchListener(this);
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
            /*case R.id.btn_speak_title:
                Intent checkIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                checkIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                checkIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                checkIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
                startActivityForResult(checkIntent, MY_TITLE_CHECK_CODE);
                break;
            case R.id.btn_speak_details:
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
                startActivityForResult(intent, MY_DETAILS_CHECK_CODE);
                break;*/
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && null != data){
            if (requestCode == MY_TITLE_CHECK_CODE) {
                    // success, create the TTS instance
                    //mTts = new TextToSpeech(this, this);
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    title.setText(result.get(0));
            } else if (requestCode == MY_DETAILS_CHECK_CODE) {
                    // success, create the TTS instance
                    //mTts = new TextToSpeech(this, this);
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    details.setText(result.get(0));
            }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        if (wrapper.getId() == null) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
            invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDialog(wrapper);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(final NotesWrapperModel notesWrapperModel) {
        new AlertDialog.Builder(AddNoteActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseTable.child(user.getUid()).child(notesWrapperModel.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                }
                                else Toast.makeText(AddNoteActivity.this,"Failed to Delete.",Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.title:
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (title.getRight() - title.getCompoundDrawables()[2].getBounds().width())) {
                        // your action here
                        Intent checkIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        checkIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        checkIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        checkIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
                        startActivityForResult(checkIntent, MY_TITLE_CHECK_CODE);
                        return true;
                    }
                }
                return false;
            case R.id.details:
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (details.getRight() - details.getCompoundDrawables()[2].getBounds().width())) {
                        // your action here
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
                        startActivityForResult(intent, MY_DETAILS_CHECK_CODE);
                        return true;
                    }
                }
                return false;
        }
        return false;
    }
}
