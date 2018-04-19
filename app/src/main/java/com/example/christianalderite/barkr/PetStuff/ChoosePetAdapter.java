package com.example.christianalderite.barkr.PetStuff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Christian Alderite on 3/31/2018.
 */

public class ChoosePetAdapter extends RecyclerView.Adapter<ChoosePetAdapter.MyViewHolder> {

    private List<PetModel> petList;
    ChoosePet choosePet;
    SharedPreferences sharedPreferences;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ageGenderBreed, description;
        public ImageView petImage;
        int position;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            name = (TextView) itemView.findViewById(R.id.petnameTV);
            ageGenderBreed = (TextView) itemView.findViewById(R.id.breedTV);
            description = (TextView) itemView.findViewById(R.id.petdescTV);
            petImage = (ImageView) itemView.findViewById(R.id.imgViewPet);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    final PetModel pet = petList.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(choosePet);
                    builder.setCancelable(false)
                            .setMessage("You are about find friends for "+pet.getName()+". Do you wish to continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(choosePet);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("currentPetId", pet.getPetId());
                                    editor.putString("currentPetImageUri", pet.getPetImageUri());
                                    editor.putString("currentPetName", pet.getName());
                                    editor.apply();

                                    choosePet.setResult(RESULT_OK);

                                    dialogInterface.dismiss();
                                    choosePet.finish();
                                    
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            });
        }
    }

    public ChoosePetAdapter(List<PetModel> petList,ChoosePet choosePet){
        this.petList = petList;
        this.choosePet = choosePet;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_pets, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PetModel pet = petList.get(position);
        holder.name.setText(pet.getName());
        holder.ageGenderBreed.setText(pet.getGender()+" "+pet.getBreed()+" born on "+pet.getBirthdate());
        holder.description.setText(pet.getOthers());
        
        Utilities.loadImage(choosePet,pet.getPetImageUri(),holder.petImage);
    }


    //Gets the size of the Array List
    @Override
    public int getItemCount() {
        return petList.size();
    }

    //Method for removing an item from the list
    public void removeItem(int position){
        this.petList.remove(position);
        this.notifyDataSetChanged();
    }
}
