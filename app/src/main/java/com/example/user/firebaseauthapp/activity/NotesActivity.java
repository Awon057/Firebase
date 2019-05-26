package com.example.user.firebaseauthapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.adapter.NotesAdapter;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;
import com.example.user.firebaseauthapp.utils.PlayDataInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private NotesAdapter mAdapter;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = NotesActivity.class.getSimpleName();
    private DatabaseReference mDatabaseTable;
    private FirebaseDatabase mInstance;
    private List<NotesWrapperModel> list;
    private Toolbar mTopToolbar;
    private StaggeredGridLayoutManager layoutManager;
    private TextToSpeech textToSpeech;
    //private LinearLayoutManager layoutManager;
    //private FlexboxLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setTitle("Notes");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);

        // use a linear layout manager
        //layoutManager = new LinearLayoutManager(this);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        /*layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);*/
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new NotesAdapter();
        recyclerView.setAdapter(mAdapter);

        initializeData();
        fab.setOnClickListener(this);
    }

    private void initializeData() {
        textToSpeech = new TextToSpeech(NotesActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(NotesActivity.this, "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }

        });
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        mInstance = FirebaseDatabase.getInstance();
        mDatabaseTable = mInstance.getReference("notes").child(user.getUid());
        // Read from the database
        mDatabaseTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot != null) {
                    list = new ArrayList<>();
                    for (DataSnapshot notes : dataSnapshot.getChildren()) {
                        NotesWrapperModel notesWrapperModel = new NotesWrapperModel();
                        notesWrapperModel.setId(notes.getKey());
                        notesWrapperModel.setNotesModel(notes.getValue(NotesModel.class));
                        list.add(notesWrapperModel);
                    }
                    mAdapter.setRecords(mDatabaseTable, list, NotesActivity.this, passData);
                } else
                    Toast.makeText(NotesActivity.this, "No data found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    PlayDataInterface passData = new PlayDataInterface() {
        @Override
        public void passData(NotesWrapperModel notesWrapperModel) {
            showDialog(notesWrapperModel);
        }
    };


    private void showDialog(final NotesWrapperModel notesWrapperModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        builder.setTitle(notesWrapperModel.getNotesModel().getTitle())
                .setMessage(notesWrapperModel.getNotesModel().getDetails())

                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent= new Intent(NotesActivity.this, AddNoteActivity.class);
                        intent.putExtra("model", notesWrapperModel);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Play", null);

                final AlertDialog mDialog = builder.create();
                mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (!textToSpeech.isSpeaking()) {
                                    String data = notesWrapperModel.getNotesModel().getTitle() + "." + notesWrapperModel.getNotesModel().getDetails();
                                    int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
                                    mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText("Stop");

                                    if (speechStatus == TextToSpeech.ERROR) {
                                        Log.e("TTS", "Error in converting Text to Speech!");
                                    }
                                } else {
                                    textToSpeech.stop();
                                    mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText("Play");
                                }
                            }
                        });
                    }
                });
                mDialog.show();
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        textToSpeech.stop();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showDialog() {
        new AlertDialog.Builder(NotesActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(NotesActivity.this, LoginActivity.class));
                        finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                NotesWrapperModel notesWrapperModel = new NotesWrapperModel();
                Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
                intent.putExtra("model", notesWrapperModel);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
