package com.example.christianalderite.barkr.ProgramStuff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewProgram extends AppCompatActivity
{
    private Button btnJoin,btnCancel;
    private ArrayList<ParticipantModel> participantsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ParticipantsAdapter pAdapter;
    private int position;
    private int currentMode;
    private String programId;
    private TextView textHost, textDescription, textLocation, textStartTime, textEndTime, textParticipantsCount;
    SharedPreferences sharedPreferences;
    String programImageUri;
    ImageView programImageView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_program);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF33B5E5));
        getSupportActionBar().setElevation(0);

        btnJoin =(Button) findViewById(R.id.join_program);
        btnCancel =(Button) findViewById(R.id.cancel_program);

        textDescription = findViewById(R.id.textDescription);
        textHost = findViewById(R.id.textHostName);
        textLocation = findViewById(R.id.textLocation);
        textStartTime = findViewById(R.id.textStartTime);
        textEndTime = findViewById(R.id.textEndTime);
        textParticipantsCount = findViewById(R.id.textParticipantsCount);
        programImageView = findViewById(R.id.programImage);
        //Set value of participants based on recyclerView adapter size

        initRecyclerView();

        Intent fromAdapter = getIntent();
        if (fromAdapter.getExtras() != null) {
            Bundle bundle = fromAdapter.getExtras();
            position = bundle.getInt("position");
            programId = bundle.getString("programId");

            currentMode = bundle.getInt("currentMode");
            if(currentMode==0){
                setUpProgramsButtons();
            }else{
                setUpYourProgramsButtons();
            }
        }

        this.fetchProgramInfo();
        this.prepareParticipants();

    }

    public void fetchProgramInfo(){
        Utilities.showLoadingDialog(this);
        final DatabaseReference refProgram = database.getReference("programs").child(programId);
        refProgram.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ProgramModel program = dataSnapshot.getValue(ProgramModel.class);
                    loadProgramInfo(program);
                }
                Utilities.dismissDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utilities.dismissDialog();
                finish();
            }
        });
    }

    public void loadProgramInfo(ProgramModel programModel){
        setTitle(programModel.getTitle());
        textDescription.setText(programModel.getDescription());
        textHost.setText(programModel.getHostDisplayName());
        textLocation.setText(programModel.getLocation());
        textStartTime.setText(programModel.getStarttime());
        textEndTime.setText(programModel.getEndtime());

        Utilities.loadImage(this, programModel.getProgramImageUri(), programImageView);
    }

    public void setUpProgramsButtons() {

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference refProgramParticipants = database.getReference("programParticipants");

                btnJoin.setText("Joined!");
                btnJoin.setEnabled(false);
                btnCancel.setEnabled(true);
                btnCancel.setVisibility(View.VISIBLE);

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewProgram.this);
                ParticipantModel you =  new ParticipantModel(user.getUid(),
                        sharedPreferences.getString("userName",""),
                        sharedPreferences.getString("userImageUri",""));

                refProgramParticipants.child(programId).child(user.getUid()).setValue(you);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ViewProgram.this)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to leave this event?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final DatabaseReference refProgramParticipants = database.getReference("programParticipants");

                                btnJoin.setEnabled(true);
                                btnJoin.setText("Join");
                                btnCancel.setEnabled(false);
                                btnCancel.setVisibility(View.INVISIBLE);

                                refProgramParticipants.child(programId).child(user.getUid()).removeValue();
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

    public void setUpYourProgramsButtons(){
        btnJoin.setText("Edit");
        btnCancel.setText("Delete");

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEdit = new Intent(ViewProgram.this, AddProgram.class);
                toEdit.putExtra("position", position);
                toEdit.putExtra("programId", programId);
                toEdit.putExtra("name", getTitle().toString());
                toEdit.putExtra("description", textDescription.getText());
                toEdit.putExtra("location", textLocation.getText());
                toEdit.putExtra("startTime", textStartTime.getText());
                toEdit.putExtra("endTime", textEndTime.getText());
                toEdit.putExtra("programImageUri", programImageUri);
                startActivity(toEdit);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ViewProgram.this)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this program?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final DatabaseReference refPrograms = database.getReference("programs");
                                final DatabaseReference refProgramParticipants = database.getReference("programParticipants");

                                refPrograms.child(programId).removeValue();
                                refProgramParticipants.child(programId).removeValue();
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

    public void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        pAdapter = new ParticipantsAdapter(participantsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);
    }

    private void prepareParticipants(){
        final DatabaseReference refProgramParticipants = database.getReference("programParticipants").child(programId);
        final DatabaseReference refParticipantsCount = database.getReference("programs").child(programId).child("participantsCount");
        refProgramParticipants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                participantsList.clear();

                if(currentMode==0){
                    if(dataSnapshot.hasChild(user.getUid())){
                        btnJoin.setEnabled(false);
                        btnJoin.setText("Joined!");
                        btnCancel.setEnabled(true);
                    }else{
                        btnJoin.setEnabled(true);
                        btnCancel.setEnabled(false);
                        btnCancel.setVisibility(View.INVISIBLE);
                    }
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ParticipantModel participant = snapshot.getValue(ParticipantModel.class);
                    participantsList.add(participant);
                }
                pAdapter.notifyDataSetChanged();
                refParticipantsCount.setValue(dataSnapshot.getChildrenCount());
                textParticipantsCount.setText(dataSnapshot.getChildrenCount()+" will attend");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
