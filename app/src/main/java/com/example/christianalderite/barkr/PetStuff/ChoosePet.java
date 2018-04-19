package com.example.christianalderite.barkr.PetStuff;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
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

public class ChoosePet extends AppCompatActivity {

    private ArrayList<PetModel> petList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChoosePetAdapter pAdapter;

    ProgressDialog dialogProgress;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pet);

        getSupportActionBar().setElevation(0);

        this.setTitle("Find friends for...");
        this.initRecyclerView();
        this.preparePetList();

    }
    //Initializes the recycler view
    public void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.yourpetsRV);
        pAdapter = new ChoosePetAdapter(petList, ChoosePet.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);
    }

    public void preparePetList(){
        Utilities.showLoadingDialog(this);

        database.goOnline();
        final DatabaseReference ref = database.getReference("pets");
        final Query refUserPets =ref.orderByChild("ownerId").equalTo(user.getUid());

        refUserPets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petList.clear();
                // TO DO: Figure out optimized way of accessing data
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PetModel pet = snapshot.getValue(PetModel.class);
                    petList.add(pet);
                }
                pAdapter.notifyDataSetChanged();
                Utilities.dismissDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               Utilities.dismissDialog();
            }
        });
    }
}
