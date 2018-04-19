package com.example.christianalderite.barkr;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    View view;
    HomeActivity main;

    TextView accountFullName, accountBio, accountEmail, accountSex, accountBirthDate;
    ImageView accountPhoto;
    Button accountEdit;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    SharedPreferences sharedPreferences;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        main = (HomeActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_account, container, false);
        main.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF669900));
        main.getSupportActionBar().setTitle("Your account profile...");

        this.setUpUI();
        this.prepareUserInfo();

        return view;
    }

    public void setUpUI(){
        accountFullName = (TextView) view.findViewById(R.id.accountFullName);
        accountBio = (TextView) view.findViewById(R.id.accountBio);
        accountEmail = (TextView) view.findViewById(R.id.accountEmail);
        accountSex = (TextView) view.findViewById(R.id.accountGender);
        accountBirthDate = (TextView) view.findViewById(R.id.accountBirthDate);

        accountPhoto = (ImageView) view.findViewById(R.id.accountPhoto);
        accountEdit = (Button) view.findViewById(R.id.accountEdit);
    }

    public void prepareUserInfo(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main);

        accountFullName.setText(sharedPreferences.getString("userName",""));
        accountBio.setText(sharedPreferences.getString("userBio",""));
        accountEmail.setText(sharedPreferences.getString("userEmail",""));
        accountSex.setText(sharedPreferences.getString("userGender",""));
        accountBirthDate.setText(sharedPreferences.getString("userBirthDate",""));

        Utilities.loadImage(main, sharedPreferences.getString("userImageUri",""), accountPhoto);

        accountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEdit = new Intent(main, EditAccount.class);
                startActivityForResult(toEdit,0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            this.prepareUserInfo();
            main.setHeaderView();
        }
    }
}
