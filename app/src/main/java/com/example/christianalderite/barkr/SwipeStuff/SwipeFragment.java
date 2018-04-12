package com.example.christianalderite.barkr.SwipeStuff;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.PetStuff.AddPet;
import com.example.christianalderite.barkr.PetStuff.ChoosePet;
import com.example.christianalderite.barkr.PetStuff.PetModel;
import com.example.christianalderite.barkr.PetStuff.ViewPet;
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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SwipeFragment extends Fragment {


    public SwipeFragment() {
        // Required empty public constructor
    }

    private View view;
    private HomeActivity main;

    private CardView cardView;
    private ImageView headerPetImage;
    private TextView headerPetName;
    private Button btnLike, btnPass;

    private cardsAdapter cardsAdapter;
    private String currentPetId, currentPetName, currentPetImageUri;
    SwipeFlingAdapterView flingContainer;

    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // List<cards> rowItems = new ArrayList<>();
    List<PetModel> rowItems = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_swipe, container, false);
        main = (HomeActivity) getActivity();

        main.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFE27816));
        main.getSupportActionBar().setElevation(0);
        main.getSupportActionBar().setTitle("Finding matches for...");

        headerPetName = (TextView) view.findViewById(R.id.headerPetName);
        headerPetImage = (ImageView) view.findViewById(R.id.imgViewPet);
        btnLike = (Button) view.findViewById(R.id.right);
        btnPass = (Button) view.findViewById(R.id.left);
        cardView = (CardView) view.findViewById(R.id.headerPetCard);
        cardView.setVisibility(View.INVISIBLE);

        this.setUpSwipe();
        this.loadPickedPet();
        return view;
    }

    public void loadPickedPet(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main);
        currentPetId = sharedPreferences.getString("currentPetId","");

        if(currentPetId!=null && !currentPetId.equals("")){
            currentPetName = sharedPreferences.getString("currentPetName","");
            currentPetImageUri = sharedPreferences.getString("currentPetImageUri","");
            this.setUpPetCard();
            this.getUnYuppedPets();
        }else{
            this.toChoosePet();
        }
    }

    public void setUpSwipe() {
        cardsAdapter = new cardsAdapter(main, R.layout.item, rowItems);
        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);
        flingContainer.setAdapter(cardsAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                cardsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //if()
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                PetModel obj = (PetModel) dataObject;
                String petId = obj.getPetId();
                database.getReference("petYups").child(petId).child(currentPetId).setValue(true);
                ifConnectionMatch(petId);
                Toast.makeText(main, "Liked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                //getUnYuppedPets();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent toViewPet = new Intent(main, FullCardActivity.class);
                PetModel obj = (PetModel) dataObject;

                toViewPet.putExtra("petId", obj.getPetId());
                toViewPet.putExtra("name", obj.getName());
                toViewPet.putExtra("breed", obj.getBreed());
                toViewPet.putExtra("gender", obj.getGender());
                toViewPet.putExtra("birthDate", obj.getBirthdate());
                toViewPet.putExtra("others", obj.getOthers());
                toViewPet.putExtra("ownerDisplayName", obj.getOwnerDisplayName());
                toViewPet.putExtra("imageUri", obj.getPetImageUri());

                startActivity(toViewPet);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    flingContainer.getTopCardListener().selectRight();
                }catch(Exception e){
                    Toast.makeText(main, "No more pets", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    flingContainer.getTopCardListener().selectLeft();
                }catch(Exception e){
                    Toast.makeText(main, "No more pets", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void toChoosePet(){
        Intent toChoosePet = new Intent(main,ChoosePet.class);
        startActivityForResult(toChoosePet,0);
    }

    public void setUpPetCard(){

        headerPetName.setText(currentPetName);

        try {
            Picasso.with(main).load(currentPetImageUri).fit().centerCrop().into(headerPetImage);
        }catch (Exception e){

        }
        main.setPetHeader(currentPetImageUri);

        cardView.setVisibility(View.VISIBLE);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChoosePet();
            }
        });
    }

    public void getUnYuppedPets(){
        main.showLoadingDialog();
        final DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                rowItems.clear();
                if(dataSnapshot.child("pets").exists()){
                    for(DataSnapshot snapshot: dataSnapshot.child("pets").getChildren()){
                        if(!snapshot.child("ownerId").getValue().equals(user.getUid())){
                            if(!dataSnapshot.child("petYups")
                                    .child(snapshot.getKey())
                                    .hasChild(currentPetId)) {
                                PetModel item = snapshot.getValue(PetModel.class);
                                //cards item = new cards(pet.getPetId(), pet.getName(), pet.getBreed(), pet.getGender(), pet.getPetImageUri());
                                rowItems.add(item);
                            }
                        }
                    }
                }
                cardsAdapter.notifyDataSetChanged();
                main.dismissDialog();
                if(rowItems.isEmpty()){
                    main.basicAlert("Nothing to show", "There are no pets to match.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                main.dismissDialog();
                main.taskFailedAlert();
            }
        });


    }

    public void ifConnectionMatch(final String userId){
        DatabaseReference ref = database.getReference("petYups").child(currentPetId).child(userId);
        final DatabaseReference refMatches = database.getReference("petMatches");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String key = database.getReference("chats").push().getKey();
                    refMatches.child(dataSnapshot.getKey()).child(currentPetId).child("chatId").setValue(key);
                    refMatches.child(currentPetId).child(dataSnapshot.getKey()).child("chatId").setValue(key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            main.swipeFragment();
        }
    }
}
