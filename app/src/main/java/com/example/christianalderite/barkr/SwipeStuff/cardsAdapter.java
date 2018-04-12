package com.example.christianalderite.barkr.SwipeStuff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.PetStuff.PetModel;
import com.example.christianalderite.barkr.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Christian Alderite on 3/14/2018.
 */

public class cardsAdapter extends ArrayAdapter<PetModel> {

    Context context;

    public cardsAdapter(Context context, int resourceId, List<PetModel> items){
        super(context, resourceId, items);

        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        // cards cardItem = getItem(position);
        PetModel cardItem = getItem(position);

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(cardItem.getName());
        description.setText(cardItem.getGender()+" "+ cardItem.getBreed());

        try {
            Picasso.with(context).load(cardItem.getPetImageUri()).fit().centerCrop().into(image);
        }catch (Exception e){
            
        }

        return convertView;

    }
}
