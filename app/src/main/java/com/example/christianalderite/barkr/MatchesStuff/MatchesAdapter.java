package com.example.christianalderite.barkr.MatchesStuff;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.MatchesStuff.ChatActivity;
import com.example.christianalderite.barkr.PetStuff.PetModel;
import com.example.christianalderite.barkr.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Christian on 3/7/2018.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MyViewHolder> {

    private List<MatchModel> matchList;
    HomeActivity main;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message;
        public ImageView image;
        int position;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            image = (ImageView) itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    MatchModel pet = matchList.get(position);
                    Intent toChatScreen = new Intent(main, ChatActivity.class);
                    toChatScreen.putExtra("matchId",pet.getMatchId());
                    main.startActivity(toChatScreen);
                }
            });
        }
    }

    public MatchesAdapter(List<MatchModel> matchList, HomeActivity main){
        this.matchList = matchList;
        this.main = main;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messages, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchesAdapter.MyViewHolder holder, int position) {
        MatchModel pet = matchList.get(position);
        holder.name.setText(pet.getMatchName()+" ("+pet.getMatchOwner()+")");
        holder.message.setText(pet.getEarliestMessage());
        try {
            Picasso.with(main).load(pet.getMatchUri()).fit().centerCrop().into(holder.image);
        }catch (Exception e){

        }
    }

    //Gets the size of the Array List
    @Override
    public int getItemCount() {
        return matchList.size();
    }

    //Method for removing an item from the list
    public void removeItem(int position){
        this.matchList.remove(position);
        this.notifyDataSetChanged();
    }
}
