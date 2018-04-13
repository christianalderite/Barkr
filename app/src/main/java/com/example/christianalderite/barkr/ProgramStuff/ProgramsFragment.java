package com.example.christianalderite.barkr.ProgramStuff;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramsFragment extends Fragment {


    public ProgramsFragment() {
        // Required empty public constructor
    }

    private View view;
    private HomeActivity main;

    private ArrayList<ProgramModel> programList = new ArrayList<>();
    //private ArrayList<ProgramModel> yourprogramList=new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgramsAdapter pAdapter;

    final int PROGRAMS = 0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_programs, container, false);
        main = (HomeActivity) getActivity();
        setHasOptionsMenu(true);
        main.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF33B5E5));
        main.getSupportActionBar().setTitle("Events...");

        this.setUpRecyclerView();

        return view;
    }

    public void setUpRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.yourprogramsRV);
        pAdapter = new ProgramsAdapter(programList, main, PROGRAMS);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(main.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterItems(s.toLowerCase());
                return true;
            }
        });
    }

    public void filterItems(String searchText){
        List<ProgramModel> temp = new ArrayList<>();
        ProgramModel p;
        for (int i = 0; i < programList.size(); i++) {
            p = programList.get(i);
            if (p.getTitle().contains(searchText)) {
                temp.add(p);
            }
        }
        pAdapter.updateList(temp);
    }

    private void prepareProgramList(){
        main.showLoadingDialog();
        final DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                programList.clear();
                for(DataSnapshot snapshot : dataSnapshot.child("programs").getChildren()){
                    if(!dataSnapshot.child("programParticipants").child(snapshot.getKey()).hasChild(user.getUid())) {
                        ProgramModel program = snapshot.getValue(ProgramModel.class);
                        programList.add(program);
                    }
                }
                pAdapter.notifyDataSetChanged();
                main.dismissDialog();
                if(programList.isEmpty()){
                    main.basicAlert("Nothing to show", "There are no programs to join.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                main.dismissDialog();
                main.taskFailedAlert();
            }
        });
    }

     @Override
    public void onStart() {
        super.onStart();
        this.prepareProgramList();
    }

}
