package com.infosys.stocktake.inventory.clubs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;

import java.util.ArrayList;

//import info.androidhive.viewpager2.R;

public class ClubFragment extends Fragment {

    private TextView textView;
    private ArrayList<Club> mClubs = new ArrayList<>();
    private static final String TAG = "Club Fragment";
    private StockTakeFirebase<Club> stockTakeFirebase;
    private String currentClub;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.inventory_list_2,container,false);
    String message = getArguments().getString("message");
    currentClub = getArguments().getString("club");
    stockTakeFirebase = new StockTakeFirebase<Club>(Club.class, "clubs");
    return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        populateItems();
    }

    private void populateItems(){
        Log.d(TAG,"Populating items...");
        Task<ArrayList<Club>> populateTask = stockTakeFirebase.getCollection();
        populateTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Club>>() {
            @Override
            public void onSuccess(ArrayList<com.infosys.stocktake.models.Club> clubs) {
                Log.d(TAG,"Accessed firebase! populating clubs now...");
                for(Club club:clubs){
                    if (!club.getClubID().matches(currentClub) && !club.getClubName().matches("Not a Club Exco")) {
//                    if (!club.getClubName().matches("Not a Club Exco")) { //change to top line after testing is done
                        mClubs.add(club);
                        Log.d(TAG, "onSuccess: Club ID is " + club.getClubID() + " and current club is " + currentClub);
                    }
                }
                initRecyclerView();
            }
        });
        populateTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to retrieve items :(, exception is: ", e);
            }
        });

    }


    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        ClubRecyclerViewAdapter recyclerAdapter = new ClubRecyclerViewAdapter(mClubs, getActivity());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
    }
}