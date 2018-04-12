package com.example.christianalderite.barkr.PetStuff;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.PetStuff.AddPet;
import com.example.christianalderite.barkr.PetStuff.PetModel;
import com.example.christianalderite.barkr.PetStuff.PetsAdapter;
import com.example.christianalderite.barkr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourPetsFragment extends Fragment {

    public YourPetsFragment() {
        // Required empty public constructor
    }

    private View view;
    private HomeActivity main;

    private ArrayList<PetModel> petList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PetsAdapter pAdapter;
    private ProgressBar progressBar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_your_pets, container, false);
        main = (HomeActivity) getActivity();
        main.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFE27816));
        main.getSupportActionBar().setElevation(0);
        main.getSupportActionBar().setTitle("Your pals...");

        this.setUpButtons();
        this.initRecyclerView();

        return view;
    }

    public void setUpButtons(){
        FloatingActionButton add = (FloatingActionButton) view.findViewById(R.id.addFAB);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAdd = new Intent(main, AddPet.class);
                startActivity(toAdd);
            }
        });
    }
    //Initializes the recycler view
    public void initRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.yourpetsRV);
        pAdapter = new PetsAdapter(petList, main);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(main.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);
    }

    //Contains initial pet data for the recycler view
    public void preparePetList(){
        main.showLoadingDialog();
        final DatabaseReference ref = database.getReference("pets");
        final Query refUserPets =ref.orderByChild("ownerId").equalTo(user.getUid());

        refUserPets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PetModel pet = snapshot.getValue(PetModel.class);
                    petList.add(pet);
                }
                pAdapter.notifyDataSetChanged();
                main.dismissDialog();
                if(petList.isEmpty()){
                    main.basicAlert("No Data to Show", "You don't have pets.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                main.dismissDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.preparePetList();
    }

}
