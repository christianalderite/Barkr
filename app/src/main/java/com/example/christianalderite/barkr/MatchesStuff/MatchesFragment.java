package com.example.christianalderite.barkr.MatchesStuff;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.PetStuff.ChoosePet;
import com.example.christianalderite.barkr.PetStuff.PetModel;
import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MatchesFragment extends Fragment {

    public MatchesFragment() {
        // Required empty public constructor
    }

    private View view;
    private HomeActivity main;

    private ArrayList<MatchModel> petMatches = new ArrayList<>();
    private CardView cardView;
    private ImageView headerPetImage;
    private TextView headerPetName;
    private RecyclerView recyclerView;
    private MatchesAdapter mAdapter;
    private String currentPetId, currentPetName, currentPetImageUri;

    SharedPreferences sharedPreferences;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_messages, container, false);
        main = (HomeActivity) getActivity();

        main.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFAA66CC));
        main.getSupportActionBar().setElevation(0);
        main.getSupportActionBar().setTitle("Messages for...");

        headerPetName = (TextView) view.findViewById(R.id.headerPetName);
        headerPetImage = (ImageView) view.findViewById(R.id.imgViewPet);
        cardView = (CardView) view.findViewById(R.id.headerPetCard);
        cardView.setVisibility(View.INVISIBLE);

        this.initRecyclerView();
        this.loadPickedPet();
        return view;
    }

    //Initializes the recycler view
    public void initRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.messagesRV);
        mAdapter = new MatchesAdapter(petMatches, main);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(main.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    public void loadPickedPet(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main);
        currentPetId = sharedPreferences.getString("currentPetId","");

        if(currentPetId!=null && !currentPetId.equals("")){
            currentPetName = sharedPreferences.getString("currentPetName","");
            currentPetImageUri = sharedPreferences.getString("currentPetImageUri","");
            this.setUpPetCard();
            this.preparePetMatches();
        }else{
            this.toChoosePet();
        }
    }

    public void preparePetMatches(){
        final DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petMatches.clear();
                for(DataSnapshot snapshot: dataSnapshot.child("pets").getChildren()){
                    if(dataSnapshot.child("petMatches").child(snapshot.getKey()).hasChild(currentPetId)){
                        String lastMsg = "Let's chat!";

                        final PetModel pet = snapshot.getValue(PetModel.class);
//                        try {
//                            lastMsg = dataSnapshot.child("chats")
//                                    .child(currentPetId)
//                                    .child(snapshot.getKey())
//                                    .child("lastMsg")
//                                    .getValue(String.class);
//                        }catch (Exception e){
//
//                        }
                        MatchModel match = new MatchModel(pet, lastMsg);
                        petMatches.add(match);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if(petMatches.isEmpty()){
                    main.basicAlert("Nothing to show","No matches for this pet yet.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                main.dismissDialog();
                main.taskFailedAlert();
            }
        });
    }

    public void toChoosePet(){
        Intent selectPet = new Intent(main, ChoosePet.class);
        startActivityForResult(selectPet,0);
    }

    public void setUpPetCard(){
        headerPetName.setText(currentPetName);
        Utilities.loadImage(main, currentPetImageUri, headerPetImage);
        main.setPetHeader(currentPetImageUri);

        cardView.setVisibility(View.VISIBLE);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChoosePet();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            main.matchesFragment();
        }
    }
}
