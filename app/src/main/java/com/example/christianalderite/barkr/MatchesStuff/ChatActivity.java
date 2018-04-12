package com.example.christianalderite.barkr.MatchesStuff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christianalderite.barkr.PetStuff.PetModel;
import com.example.christianalderite.barkr.PetStuff.ViewPet;
import com.example.christianalderite.barkr.ProgramStuff.ProgramModel;
import com.example.christianalderite.barkr.ProgramStuff.ProgramsAdapter;
import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ArrayList<ChatModel> chatList = new ArrayList<>();
    private ChatAdapter cAdapter;

    private EditText typeMessage;
    private Button btnSend;
    private String currentPetId, matchId, chatId;
    ProgressDialog dialogProgress;
    SharedPreferences sp;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    DatabaseReference refChat;
    DatabaseReference refLastMsg;

    TextView petdescTV;
    TextView petnameTV;
    TextView breedTV;
    ImageView petImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFAA66CC));
        getSupportActionBar().setElevation(0);

        petdescTV = findViewById(R.id.petdescTV);
        petnameTV = findViewById(R.id.petnameTV);
        breedTV = findViewById(R.id.breedTV);
        petImage = findViewById(R.id.imgViewPet);

        if(getIntent().getExtras()!=null){
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            matchId = getIntent().getExtras().getString("matchId");
            currentPetId = sp.getString("currentPetId","");
        }else {
            finish();
        }

        this.getMatchInfo();
        this.setUpButtons();

        ref = database.getReference("petMatches").child(currentPetId).child(matchId).child("chatId");
        refLastMsg = database.getReference("petMatches").child(currentPetId).child(matchId).child("lastMsg");
        refChat = database.getReference("chats");

        this.getChatId();
        this.setUpRecyclerView();



    }

    public void getMatchInfo(){
        Utilities.showLoadingDialog(this);
        final DatabaseReference refMatch = database.getReference("pets").child(matchId);
        refMatch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Utilities.dismissDialog();
                if(dataSnapshot.exists()){
                    PetModel pet = dataSnapshot.getValue(PetModel.class);
                    breedTV.setText(pet.getGender()+" "+pet.getBreed() +" born on "+pet.getBirthdate());
                    petdescTV.setText(pet.getOthers());
                    petnameTV.setText(pet.getName());

                    try{
                        Picasso.with(ChatActivity.this)
                        .load(pet.getPetImageUri()).fit().centerCrop().into(petImage);
                    }catch (Exception e){

                    }
                }else{
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(){

        String temp = typeMessage.getText().toString();
        if(!temp.isEmpty()){
            DatabaseReference refNewMessage = refChat.push();

            Map<String, String> newMessage = new HashMap<String, String>();
            newMessage.put("senderId", currentPetId);
            newMessage.put("message", temp);
            refNewMessage.setValue(newMessage);
            refLastMsg.setValue(temp);
        }
        typeMessage.setText(null);
    }

    public void getChatId(){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    refChat = refChat.child(chatId);
        
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getChatMessages(){
        refChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String senderId = null;

                    if(dataSnapshot.child("message").getValue()!=null){
                        message = dataSnapshot.child("message").getValue().toString();
                    }
                    if(dataSnapshot.child("senderId").getValue()!=null){
                        senderId = dataSnapshot.child("senderId").getValue().toString();
                    }
                    if(message!=null && senderId!=null){
                        Boolean currentPetBoolean = false;
                        if(senderId.equals(currentPetId)){
                            currentPetBoolean =true;
                        }
                
                        ChatModel newMessage = new ChatModel(message, currentPetBoolean);
                        chatList.add(newMessage);
                        cAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
        });
    }

    public void setUpButtons(){
        typeMessage = (EditText) findViewById(R.id.typeMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void setUpRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.chatRV);
        cAdapter = new ChatAdapter(chatList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);
    }

}
