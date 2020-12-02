package com.infosys.stocktake.inventory.clubs;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.items.ItemRecyclerViewAdapter;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.QrCode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewClubActivity extends AppCompatActivity {
    private Club club;
    private ArrayList<String> mItemNames = new ArrayList<>();
    private ArrayList<String> mItemDescriptions= new ArrayList<>();
    private ArrayList<String> mImages= new ArrayList<>();
    private ArrayList<Item> mItems = new ArrayList<>();
    private Item item;
    private boolean isAdmin = false;
    StockTakeFirebase<Item> itemStockTakeFirebase;
    private static final String TAG = "ViewClub: ";
    TextView clubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_list);
        // Populate components with Item data from passed Intent
        club = (Club) getIntent().getSerializableExtra("ClubIntent");
        clubName = findViewById(R.id.clubName);
        clubName.setText(club.getClubName());
        itemStockTakeFirebase = new StockTakeFirebase<Item>(Item.class, "items");
        populateItems();
    }

    private void populateItems(){
        Log.d(TAG,"Populating items...");
        Task<ArrayList<Item>> populateTask = itemStockTakeFirebase.compoundQuery("clubID", club.getClubID());
        populateTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Item>>() {
            @Override
            public void onSuccess(ArrayList<com.infosys.stocktake.models.Item> items) {
                if(items != null) {
                    mItems = items;
                }
                else{
                    mItems = new ArrayList<Item>();
                }
                Log.d(TAG,"populateItems: Accessed firebase! populating items now... " + mItems.size());
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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ItemRecyclerViewAdapter recyclerAdapter = new ItemRecyclerViewAdapter(mItems, isAdmin, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}