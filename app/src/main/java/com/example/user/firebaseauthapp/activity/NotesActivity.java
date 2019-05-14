package com.example.user.firebaseauthapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.adapter.NotesAdapter;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    private LinearLayoutManager layoutManager;
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
        layoutManager = new LinearLayoutManager(this);
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
                    mAdapter.setRecords(mDatabaseTable, user, list, NotesActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        //mAdapter.setRecords(list,NotesActivity.this);
    }
}
