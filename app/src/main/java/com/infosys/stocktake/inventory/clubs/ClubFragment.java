package com.infosys.stocktake.inventory.clubs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

//import info.androidhive.viewpager2.R;

public class ClubFragment extends Fragment {

    private TextView textView;
    private ArrayList<Club> mClubs;
    private User currentUser;
    private static final String TAG = "Club Fragment";
    private StockTakeFirebase<Club> stockTakeFirebase;
    private String currentClub;
    private Map<String, Membership> userClubs;
    private List<String> clubsId;
    private Button contactAdmin;
    SwipeRefreshLayout outgoingRequestsSwipeRefreshLayout;
    private List<String> adminList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.inventory_list_2,container,false);
    String message = getArguments().getString("message");
    currentClub = getArguments().getString("club");
    currentUser = (User) getArguments().getSerializable("user");
    userClubs = currentUser.getClubMembership();
    clubsId = new ArrayList<String>(userClubs.keySet());

    Log.d(TAG, "clubs len: " + clubsId.size());
    Log.d(TAG, "clubs len: " + clubsId.toString());

    stockTakeFirebase = new StockTakeFirebase<Club>(Club.class, "clubs");

    outgoingRequestsSwipeRefreshLayout = view.findViewById(R.id.clubSwipeRefresh);
    outgoingRequestsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.i(TAG, "onRefresh called from club SwipeRefreshLayout");
            populateItems();
            Log.i(TAG, "onRefresh done");
        }
    });

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
                mClubs = new ArrayList<>();
                for(Club club:clubs){
                    if (!clubsId.contains(club.getClubID()) && !mClubs.contains(club)) {
//                    if (!club.getClubName().matches("Not a Club Exco")) { //change to top line after testing is done
                        mClubs.add(club);
                        Log.d(TAG, "onSuccess: Club ID is " + club.getClubID() + " and current club is " + currentClub);
                        adminList = club.getAdminList();
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

        outgoingRequestsSwipeRefreshLayout.setRefreshing(false);

    }


    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        TextView emptyView = (TextView) getView().findViewById(R.id.empty_view);
        ClubRecyclerViewAdapter recyclerAdapter = new ClubRecyclerViewAdapter(mClubs, getActivity());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        if (mClubs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }
}