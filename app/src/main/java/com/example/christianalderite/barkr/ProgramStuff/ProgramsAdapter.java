package com.example.christianalderite.barkr.ProgramStuff;

/**
 * Created by Christian Alderite on 3/9/2018.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.MyViewHolder> {

    private final int currentMode;
    private List<ProgramModel> programList;
    HomeActivity yourPrograms;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, location, date, starttime, endtime, participants, fromDateToDate, desc;
        public int position;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.titleTV);
            location = (TextView) itemView.findViewById(R.id.locationTV);
            fromDateToDate = (TextView) itemView.findViewById(R.id.durationTV);
            participants = (TextView) itemView.findViewById(R.id.participantsTV);
            desc = (TextView) itemView.findViewById(R.id.descTV);
            imageView = (ImageView) itemView.findViewById(R.id.programHeader);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        position = getAdapterPosition();
                        ProgramModel program = programList.get(position);

                        Intent toViewProgram = new Intent(yourPrograms, ViewProgram.class);
                        toViewProgram.putExtra("programId", program.getProgramId());

                        if(currentMode==0){
                            toViewProgram.putExtra("currentMode",currentMode); //programs
                        }else{
                            toViewProgram.putExtra("currentMode",1); //your programs
                        }

                        yourPrograms.startActivity(toViewProgram);
                    }
                });

        }
    }

    public ProgramsAdapter(List<ProgramModel> programList, HomeActivity yourPrograms, int currentMode){
        this.programList = programList;
        this.yourPrograms = yourPrograms;
        this.currentMode = currentMode;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_programs, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProgramModel program = programList.get(position);
        holder.title.setText(program.getTitle());
        holder.location.setText(program.getLocation());
        holder.fromDateToDate.setText(program.getStarttime() + " to " + program.getEndtime());
        holder.desc.setText(program.getDescription());
        holder.participants.setText(program.getParticipantsCount()+" will go...");
        try{
            Picasso.with(yourPrograms).load(program.getProgramImageUri()).fit().centerCrop().into(holder.imageView);
        }catch (Exception e){

        }

    }

    //Gets the size of the Array List
    @Override
    public int getItemCount() {
        return programList.size();
    }

    //Method for removing an item from the list
    public void removeItem(int position){
        this.programList.remove(position);
        this.notifyDataSetChanged();
    }

    public void updateList(List<ProgramModel> programList){
    	this.programList=programList;
    	this.notifyDataSetChanged();
    }

    public void addItem(ProgramModel object){
        programList.add(object);
        notifyDataSetChanged();
    }

    public void clearItems(){
        programList.clear();
    }

    public boolean isEmpty(){
        return programList.isEmpty();
    }
}
