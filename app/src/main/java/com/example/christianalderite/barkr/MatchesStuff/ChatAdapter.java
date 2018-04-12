package com.example.christianalderite.barkr.MatchesStuff;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.christianalderite.barkr.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Card;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{

    private List<ChatModel> chatList;
    ChatActivity chatActivity;
    public CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
    public CardView.LayoutParams myParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message;
        public ImageView image;
        public CardView container;


        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            container = (CardView) itemView.findViewById(R.id.container);
            params.setMarginEnd(128);
            myParams.setMarginStart(128);
        }
    }

    public ChatAdapter(List<ChatModel> chatList, ChatActivity chatActivity){
        this.chatList = chatList;
        this.chatActivity = chatActivity;
    }

    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat, parent, false);
        return new ChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.MyViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        holder.message.setText(chat.getMessage());
        if(chat.getCurrentPet()){
            holder.message.setGravity(Gravity.END);
            holder.message.setTextColor(Color.parseColor("#FFFFFFFF"));
            holder.container.setBackgroundColor(Color.parseColor("#FFAA66CC"));
            holder.container.setLayoutParams(myParams);
        }else{
            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.parseColor("#FFAA66CC"));
            holder.container.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            holder.container.setLayoutParams(params);
        }
    }

    //Gets the size of the Array List
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    //Method for removing an item from the list
    public void removeItem(int position){
        this.chatList.remove(position);
        this.notifyDataSetChanged();
    }
}
