package com.example.christianalderite.barkr.PetStuff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewPet extends AppCompatActivity{
    private Button edit, delete;
    private TextView petname, petgender, petdesc, petbirthdate, petbreed, owner;
    private ImageView petImage;
    private int position;
    private String petId, name, ownerDisplayName, petImageUri;
    private boolean isLastPet;

    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);
        getSupportActionBar().setElevation(0);

        petImage = findViewById(R.id.imgPetHeader);

        petgender = findViewById(R.id.textGender);
        petdesc = findViewById(R.id.textDescription);
        petbirthdate = findViewById(R.id.textBirthDate);
        petbreed = findViewById(R.id.textBreed);
        owner = findViewById(R.id.textPetOwner);

        edit = findViewById(R.id.btnEdit);
        delete = findViewById(R.id.btnDelete);

        Intent fromAdapter = getIntent();
        if (fromAdapter.getExtras() != null) {
            Bundle bundle = fromAdapter.getExtras();
            position = bundle.getInt("position");
            name = bundle.getString("name");

            petId = bundle.getString("petId");
            petImageUri = bundle.getString("imageUri");

            petbreed.setText(bundle.getString("breed"));
            petbirthdate.setText(bundle.getString("birthDate"));
            petdesc.setText(bundle.getString("others"));
            petgender.setText(bundle.getString("gender"));
            owner.setText(bundle.getString("ownerDisplayName"));

            Utilities.loadImage(this, petImageUri, petImage);

            isLastPet = bundle.getBoolean("lastPet");

            this.setTitle(name);
            this.setUpButtons();
        }

    }
    public void setUpButtons(){
        if(isLastPet){
            delete.setEnabled(false);
        }else{
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ViewPet.this)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to delete your pet from Barkr forever?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewPet.this);
                                    if(sharedPreferences.getString("currentPetId","").equals(petId)){
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("currentPetId", "");
                                        editor.apply();
                                    }

                                    DatabaseReference ref = database.getReference("pets");
                                    ref.child(petId).removeValue();
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).show();
                }
            });
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEdit = new Intent(ViewPet.this, AddPet.class);
                toEdit.putExtra("position", position);
                toEdit.putExtra("petId", petId);
                toEdit.putExtra("name", name);
                toEdit.putExtra("breed", petbreed.getText());
                toEdit.putExtra("gender", petgender.getText());
                toEdit.putExtra("birthDate", petbirthdate.getText());
                toEdit.putExtra("others", petdesc.getText());
                toEdit.putExtra("imageUri", petImageUri);

                startActivity(toEdit);
                finish();
            }
        });
    }
}