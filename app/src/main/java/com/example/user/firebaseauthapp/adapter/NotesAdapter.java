package com.example.user.firebaseauthapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;
import com.example.user.firebaseauthapp.utils.PlayDataInterface;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
        private final ImageButton shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.show_title);
            details = (TextView) itemView.findViewById(R.id.show_details);
            shareButton = (ImageButton) itemView.findViewById(R.id.share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showDialog(list.get(getAdapterPosition()));
                    //generateNoteOnSD(context,list.get(getAdapterPosition()).getNotesModel().getTitle(),list.get(getAdapterPosition()).getNotesModel().getDetails());
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

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(context.getFilesDir(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + "abc.txt");
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            context.startActivity(Intent.createChooser(sharingIntent, "share file with"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
