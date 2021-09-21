package com.infosys.stocktake.inventory.clubs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.HomeActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
//import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.inventory.items.ItemDetailsActivity;
import com.infosys.stocktake.inventory.items.ItemRecyclerViewAdapter;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.QrCode;
import com.infosys.stocktake.models.Request;
import com.infosys.stocktake.models.RequestStatus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewClubActivity extends AppCompatActivity {
    private Club club;
    private ArrayList<Item> mItems = new ArrayList<>();
    private Item item;
    private boolean isAdmin = false;
    private Button sendAdminRequestBtn;
    StockTakeFirebase<Item> itemStockTakeFirebase;
    private static final String TAG = "ViewClub: ";
    TextView clubName;
    String userId;
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StockTakeFirebase<Request> requestStockTakeFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_list);
        userId = fbAuth.getCurrentUser().getUid();
        requestStockTakeFirebase = new StockTakeFirebase<Request>(Request.class, "requests");

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
                    for(Item item: items){
                        if(item.getIsPublic()){
                            mItems.add(item);
                        }
                    }
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
        ClubItemRecyclerViewAdapter recyclerAdapter = new ClubItemRecyclerViewAdapter(mItems, isAdmin,club, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        Intent clubIntent = new Intent(ViewClubActivity.this, HomeActivity.class);
        clubIntent.putExtra("toClub", true);
        startActivity(clubIntent);
    }


}