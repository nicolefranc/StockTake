package com.infosys.stocktake.inventory.items;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.inventory.items.ItemRecyclerViewAdapter;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import info.androidhive.viewpager2.R;

public class InventoryFragment extends Fragment {

    private TextView textView;
    private ArrayList<String> mItemNames = new ArrayList<>();
    private ArrayList<String> mItemDescriptions= new ArrayList<>();
    private ArrayList<String> mImages= new ArrayList<>();
    private StockTakeFirebase<Item> itemStockTakeFirebase;
    private StockTakeFirebase<User> userStockTakeFirebase;
    private ArrayList<Item> mItems = new ArrayList<>();
    private static final String TAG = "Inventory Fragment: ";
    private User currentUser;
    private String currentClub;
    private FloatingActionButton fab_add_item;
    private boolean isAdmin = true;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.inventory_list,container,false);
    String message = getArguments().getString("message");
    currentUser = (User) getArguments().getSerializable("user");
    currentClub = getArguments().getString("club");
    Log.d(TAG, "onCreateView: currentClub is " + currentClub);
    itemStockTakeFirebase = new StockTakeFirebase<Item>(Item.class, "items");
    userStockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");

    // Floating Action Button that directs to Add Item Activity
    fab_add_item = (FloatingActionButton) view.findViewById(R.id.fab_add_item);
    fab_add_item.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), AddItemActivity.class);
            intent.putExtra("club", currentClub);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        }
    });
    return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateItems();
        initRecyclerView();
    }

    private void populateItems(){
        Log.d(TAG,"Populating items...");
        Task<ArrayList<com.infosys.stocktake.models.Item>> populateTask = itemStockTakeFirebase.compoundQuery("clubID", currentClub);
        populateTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Item>>() {
            @Override
            public void onSuccess(ArrayList<com.infosys.stocktake.models.Item> items) {
                Log.d(TAG,"Accessed firebase! Club is " + currentClub + ". populating items now...");
                if(items != null) {
                    mItems = items;
                }
                else{
                    mItems = new ArrayList<Item>();
                }
                Log.d(TAG, "Items added: " + mItems.toString());
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
        Log.d(TAG,"Initializing recycler view...");
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        ItemRecyclerViewAdapter recyclerAdapter = new ItemRecyclerViewAdapter(mItems, isAdmin, getActivity());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
    }
}