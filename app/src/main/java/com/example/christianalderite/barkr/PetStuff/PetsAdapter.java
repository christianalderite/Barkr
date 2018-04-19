package com.example.christianalderite.barkr.PetStuff;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Christian on 3/7/2018.
 */

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.MyViewHolder> {

    private List<PetModel> petList;
    HomeActivity yourPets;

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
                    PetModel pet = petList.get(position);
                    Intent toViewPet = new Intent(yourPets, ViewPet.class);
                    toViewPet.putExtra("position", position);
                    toViewPet.putExtra("petId", pet.getPetId());
                    toViewPet.putExtra("name", pet.getName());
                    toViewPet.putExtra("breed", pet.getBreed());
                    toViewPet.putExtra("gender", pet.getGender());
                    toViewPet.putExtra("birthDate", pet.getBirthdate());
                    toViewPet.putExtra("others", pet.getOthers());
                    toViewPet.putExtra("ownerDisplayName", pet.getOwnerDisplayName());
                    toViewPet.putExtra("imageUri", pet.getPetImageUri());

                    if(getItemCount()==1){
                        toViewPet.putExtra("lastPet",true);
                    }else{
                        toViewPet.putExtra("lastPet", false);
                    }
                    //yourPets.startActivityForResult(toViewPet, 2);
                    yourPets.startActivity(toViewPet);
                }
            });
        }
    }

    public PetsAdapter(List<PetModel> petList, HomeActivity yourPets){
        this.petList = petList;
        this.yourPets = yourPets;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_pets, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PetsAdapter.MyViewHolder holder, int position) {
        PetModel pet = petList.get(position);
        holder.name.setText(pet.getName());
        holder.ageGenderBreed.setText(pet.getGender()+" "+pet.getBreed()+" born on "+pet.getBirthdate());
        holder.description.setText(pet.getOthers());

        try{
            Picasso.with(yourPets).load(pet.getPetImageUri()).fit().centerCrop().into(holder.petImage);
        }catch (Exception e){

        }
        Utilities.loadImage(yourPets, pet.getPetImageUri(),holder.petImage);

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
