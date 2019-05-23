package com.example.user.firebaseauthapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.activity.AddNoteActivity;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;
import com.example.user.firebaseauthapp.utils.PlayDataInterface;
import com.example.user.firebaseauthapp.utils.SwipeListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<NotesWrapperModel> list;
    private Context context;
    private DatabaseReference mDatabaseTable;
    private PlayDataInterface passData;

    public void setRecords(DatabaseReference mDatabaseTable, List<NotesWrapperModel> list, Context context, PlayDataInterface passData) {
        this.list = list;
        this.context = context;
        this.passData = passData;
        this.mDatabaseTable = mDatabaseTable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_row, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(list.get(i).getNotesModel().getTitle());
        viewHolder.details.setText(list.get(i).getNotesModel().getDetails());
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            if (list.size() > 0)
                return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView details;
        private final ImageButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.show_title);
            details = (TextView) itemView.findViewById(R.id.show_details);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_button);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(list.get(getAdapterPosition()));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passData.passData(list.get(getAdapterPosition()));
                }
            });
        }
    }

    private void showDialog(final NotesWrapperModel notesWrapperModel) {
        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseTable.child(notesWrapperModel.getId()).removeValue();
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
