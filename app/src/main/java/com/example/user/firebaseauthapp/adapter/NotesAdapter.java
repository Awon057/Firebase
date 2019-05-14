package com.example.user.firebaseauthapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.activity.AddNoteActivity;
import com.example.user.firebaseauthapp.model.NotesModel;
import com.example.user.firebaseauthapp.model.NotesWrapperModel;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<NotesWrapperModel> list;
    private Context context;

    public void setRecords(List<NotesWrapperModel> list, Context context) {
        this.list = list;
        this.context = context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.show_title);
            details = (TextView) itemView.findViewById(R.id.show_details);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context, AddNoteActivity.class);
                    intent.putExtra("model", list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
