package com.example.christianalderite.barkr.SwipeStuff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.firebase.database.FirebaseDatabase;

public class FullCardActivity extends AppCompatActivity {

    SharedPreferences sp;
    String petId;
    String currentPetId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ImageView petImage;
    TextView petgender;
    TextView petdesc;
    TextView petbirthdate;
    TextView petbreed;
    TextView owner, petname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_card);

        petImage = findViewById(R.id.imgPetHeader);
        petgender = findViewById(R.id.textGender);
        petdesc = findViewById(R.id.textDescription);
        petbirthdate = findViewById(R.id.textBirthDate);
        petbreed = findViewById(R.id.textBreed);
        owner = findViewById(R.id.textPetOwner);
        petname = findViewById(R.id.petName);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentPetId = sp.getString("currentPetId","");

        Intent fromAdapter = getIntent();
        if (fromAdapter.getExtras() != null) {
            Bundle bundle = fromAdapter.getExtras();

            petId = bundle.getString("petId");

            petname.setText(bundle.getString("name"));
            petbreed.setText(bundle.getString("breed"));
            petbirthdate.setText(bundle.getString("birthDate"));
            petdesc.setText(bundle.getString("others"));
            petgender.setText(bundle.getString("gender"));
            owner.setText(bundle.getString("ownerDisplayName"));
            Utilities.loadImage(this, bundle.getString("imageUri"), petImage);

        }else{
            finish();
        }

    }
}
