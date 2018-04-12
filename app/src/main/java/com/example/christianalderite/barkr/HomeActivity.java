package com.example.christianalderite.barkr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christianalderite.barkr.IntroStuff.AddFirstPet;
import com.example.christianalderite.barkr.IntroStuff.Login;
import com.example.christianalderite.barkr.MatchesStuff.MatchesFragment;
import com.example.christianalderite.barkr.PetStuff.YourPetsFragment;
import com.example.christianalderite.barkr.ProgramStuff.JoinedProgramsFragment;
import com.example.christianalderite.barkr.ProgramStuff.ProgramsFragment;
import com.example.christianalderite.barkr.ProgramStuff.YourProgramsFragment;
import com.example.christianalderite.barkr.SwipeStuff.SwipeFragment;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreferences;
    NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    View headerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getString("userId","").equals(user.getUid())){
            this.loadUserInformation();
        }else {
            this.setHeaderView();
            if(sharedPreferences.getBoolean("hasPet",false)==false){
                this.checkIfHasPet();
            }
        }
    }

    public void loadUserInformation(){
        Utilities.showLoadingDialog(this);
        final DatabaseReference ref = database.getReference("users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Utilities.dismissDialog();
                if(dataSnapshot.exists()){
                    UserModel you = dataSnapshot.getValue(UserModel.class);
                    initSharedPrefs(you);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utilities.dismissDialog();
            }
        });
    }

    public void initSharedPrefs(UserModel userModel){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userModel.getUid());
        editor.putString("userName", userModel.getDisplayName());
        editor.putString("userImageUri", userModel.getPhotoUri());
        editor.putString("userBio", userModel.getBio());
        editor.putString("userEmail", userModel.getEmail());
        editor.putString("userBirthDate", userModel.getBirthDate());
        editor.putString("userGender", userModel.getGender());
        editor.apply();

        this.setHeaderView();
        if(sharedPreferences.getBoolean("hasPet",false)==false){
            this.checkIfHasPet();
        }
    }

    public void setHeaderView(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        headerView = navigationView.getHeaderView(0);
        ImageView personPhoto = headerView.findViewById(R.id.personPhoto);
        TextView personFullName = headerView.findViewById(R.id.personFullName);
        TextView personEmail = headerView.findViewById(R.id.personEmail);

        try {
            Picasso.with(this).load(sharedPreferences.getString("userImageUri", "")).fit().centerCrop().into(personPhoto);
        }catch (Exception e){
        }

        personFullName.setText(sharedPreferences.getString("userName", ""));
        personEmail.setText(sharedPreferences.getString("userEmail", ""));

    }

    public void setPetHeader(String uri){
        headerView = navigationView.getHeaderView(0);
        ImageView petPhoto = headerView.findViewById(R.id.petPhoto);
        try {
            Picasso.with(this).load(uri).fit().centerCrop().into(petPhoto);
        }catch (Exception e){
            petPhoto.setImageResource(R.drawable.splash_logo);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnMessages) {
            matchesFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_your_pets) {
            YourPetsFragment fragment = new YourPetsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Your Pets");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            setTitle("Your pals");
        }
        if (id == R.id.nav_main) {
            swipeFragment();
        }
        if (id == R.id.nav_your_programs) {
            YourProgramsFragment fragment = new YourProgramsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Your Programs");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            setTitle("Events you created");
        }
        if (id == R.id.nav_program) {
            ProgramsFragment fragment = new ProgramsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Programs");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            setTitle("Events");
        }
        if (id == R.id.nav_joined_programs) {
            JoinedProgramsFragment fragment = new JoinedProgramsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Joined Programs");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            setTitle("Events you've joined");
        }
        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(this, Login.class);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(loginActivity);
            this.finish();
        }
        if(id == R.id.nav_account){
            AccountFragment fragment = new AccountFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Account");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            setTitle("Profile");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void swipeFragment() {
        SwipeFragment fragment = new SwipeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Match Pets");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTitle("Finding matches for...");
    }

    public void matchesFragment(){
        MatchesFragment fragment = new MatchesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Your Pets");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTitle("Messages for...");
    }

    public void basicAlert(String title, String message){
        Utilities.basicAlert(this, title, message);
    }

    public void showLoadingDialog(){
        Utilities.showLoadingDialog(this);
    }

    public void dismissDialog(){
        Utilities.dismissDialog();
    }

    public void taskFailedAlert(){
        Utilities.taskFailedAlert(this);
    }

    public void checkIfHasPet() {
        showLoadingDialog();
        DatabaseReference refPets = database.getReference("pets");
        final Query refFirstUserPet = refPets.orderByChild("ownerId").equalTo(auth.getCurrentUser().getUid());
        refFirstUserPet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dismissDialog();
                if (!dataSnapshot.exists()) {
                    Intent toAddFirstPet = new Intent(HomeActivity.this, AddFirstPet.class);
                    startActivity(toAddFirstPet);
                    finish();
                }else{
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("userHasPet",true);
                    editor.apply();
                    swipeFragment();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissDialog();
                taskFailedAlert();
            }
        });
    }

}
