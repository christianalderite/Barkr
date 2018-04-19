package com.example.christianalderite.barkr.ProgramStuff;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;

import java.util.List;

/**
 * Created by Christian Alderite on 3/13/2018.
 */

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder> {

    private List<ParticipantModel> participantsList;
    ViewProgram viewProgram;

    public ParticipantsAdapter(List<ParticipantModel> participantsList, ViewProgram viewProgram) {
        this.participantsList = participantsList;
        this.viewProgram = viewProgram;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_participants, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ParticipantModel participant = participantsList.get(position);
        holder.name.setText(participant.getDisplayName());

        Utilities.loadImage(viewProgram, participant.getImageUri(), holder.image);
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }
}
